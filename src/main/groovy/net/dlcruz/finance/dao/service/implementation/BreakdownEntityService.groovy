package net.dlcruz.finance.dao.service.implementation

import groovy.time.TimeCategory
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Breakdown
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.BreakdownService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.TransactionService
import org.joda.time.Days
import org.joda.time.Interval
import org.joda.time.Months
import org.joda.time.Weeks
import org.joda.time.Years
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BreakdownEntityService implements BreakdownService {

    @Autowired
    BudgetService budgetService

    @Autowired
    AccountService accountService

    @Autowired
    AllocationService allocationService

    @Autowired
    TransactionService transactionService

    @Override
    Breakdown getTotalBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        def label = account ? account.name : 'All Accounts'
        getTotalBreakdownFor(frequency, accounts, label)
    }

    private Breakdown getTotalBreakdownFor(Frequency frequency, List<Account> accounts, String label) {
        def breakdown = new Breakdown()

        if (accounts.empty) {
            return breakdown
        }

        def accountBudgets = accounts.collectMany(budgetService.&findAllByAccount)
        def startingDate = getStartingDateForBreakdown(frequency)
        def allocations = accounts.collectMany(allocationService.&findAllByAccountStartingFrom.rcurry(startingDate))
        def totalCredit = accounts.collect(allocationService.&getOverallCredit).sum()
        def totalDebit = accounts.collect(allocationService.&getOverallDebit).collect { it * (-1) }.sum()
        def firstTransactionDate = accounts.collect(transactionService.&getFirstTransactionDate).min()
        def lastTransactionDate = accounts.collect(transactionService.&getLastTransactionDate).max()

        return breakdown.with {
            it.label = label
            it.balance = accounts*.balance.sum()
            it.totalDebit = calculateTotalDebit(allocations)
            it.totalCredit = calculateTotalCredit(allocations)
            it.allocatedAmount = accountBudgets.collect(this.&getAllocatedAmount.rcurry(frequency)).sum()
        }.with(this.&setRates.curry(frequency, totalCredit, totalDebit, firstTransactionDate, lastTransactionDate))
    }

    @Override
    List<Breakdown> getBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        accounts.collectMany(this.&getBreakdownBy.curry(frequency))
    }

    private List<Breakdown> getBreakdownBy(Frequency frequency, Account account) {
        def accountBudgets = budgetService.findAllByAccount(account)
        def startingDate = getStartingDateForBreakdown(frequency)
        def accountAllocations = allocationService.findAllByAccountStartingFrom(account, startingDate)
        accountAllocations.groupBy { it.name }
                .collect(this.&createBreakdown.curry(frequency, account, accountBudgets))
                .collect(this.&setAllocationRates.curry(frequency, account))
    }

    private Breakdown createBreakdown(Frequency frequency, Account account, List<Budget> accountBudgets,
                                      String name, List<Allocation> allocations) {

        def budget = accountBudgets.find { it.name == name }

        new Breakdown(
            account: account,
            budget: budget,
            label: name,
            balance: allocationService.getAllocationBalance(account, name),
            totalDebit: calculateTotalDebit(allocations),
            totalCredit: calculateTotalCredit(allocations),
            allocatedAmount: getAllocatedAmount(budget, frequency)
        )
    }

    private Date getStartingDateForBreakdown(Frequency frequency) {
        use (TimeCategory) {
            switch (frequency) {
                case Frequency.DAILY:
                    return 1.day.ago
                case Frequency.WEEKLY:
                    return 1.week.ago
                case Frequency.FORTNIGHTLY:
                    return 2.weeks.ago
                case Frequency.MONTHLY:
                    return 1.month.ago
                case Frequency.ANNUALLY:
                    return 1.year.ago
            }
        }
    }

    private BigDecimal getAllocatedAmount(Budget budget, Frequency frequency) {
        budget?.with(budgetService.&convert.rcurry(frequency) >> budgetService.&round)?.amount ?: 0
    }

    private Breakdown setAllocationRates(Frequency frequency, Account account, Breakdown breakdown) {
        def overallCredit = allocationService.getOverallCredit(account, breakdown.label)
        def overallDebit = allocationService.getOverallDebit(account, breakdown.label) * (-1)
        def first = allocationService.getFirstTransactionDate(account, breakdown.label)
        def last = allocationService.getlastTransactionDate(account, breakdown.label)
        breakdown.with(this.&setRates.curry(frequency, overallCredit, overallDebit, first, last))
    }

    private Breakdown setRates(Frequency frequency, BigDecimal credit, BigDecimal debit, Date first, Date last, Breakdown breakdown) {
        def duration = getDuration(first, last, frequency)
        def divisor = [1, duration].max()
        breakdown.incomeRate = credit / divisor
        breakdown.expenseRate = debit / divisor
        breakdown
    }

    private int getDuration(Date first, Date last, Frequency frequency) {

        def interval = new Interval(first.clearTime().time, last.clearTime().time)

        switch (frequency) {
            case Frequency.DAILY:
                return Days.daysIn(interval).days
            case Frequency.WEEKLY:
                return Weeks.weeksIn(interval).weeks
            case Frequency.FORTNIGHTLY:
                return Weeks.weeksIn(interval).weeks / 2
            case Frequency.MONTHLY:
                return Months.monthsIn(interval).months
            case Frequency.ANNUALLY:
                return Years.yearsIn(interval).years
        }
    }

    private BigDecimal calculateTotalCredit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount >= 0 })
    }

    private BigDecimal calculateTotalDebit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount < 0 }) * (-1)
    }
}
