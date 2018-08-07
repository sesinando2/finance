package net.dlcruz.finance.dao.service.implementation

import net.dlcruz.finance.dao.domain.*
import net.dlcruz.finance.dao.service.*
import net.dlcruz.finance.service.FrequencyService
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

    @Autowired
    FrequencyService frequencyService

    @Override
    Breakdown getTotalBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        def label = account ? account.name : 'All Accounts'
        def start = frequencyService.getStartDateForBreakdown(frequency)
        def roundedDownStartDate = frequencyService.getRoundedDownStartDate(start, frequency)
        getTotalBreakdownFor(frequency, roundedDownStartDate,  new Date(), accounts, label)
    }

    @Override
    List<Breakdown> getBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        def start = frequencyService.getStartDateForBreakdown(frequency)
        def roundedDownStartDate = frequencyService.getRoundedDownStartDate(start, frequency)
        accounts.collectMany(this.&getBreakdownBy.curry(frequency, roundedDownStartDate, new Date()))
    }

    @Override
    List<Breakdown> getTrendsFrom(Frequency frequency, int ago) {
        getTrendsFrom(frequency, null, ago)
    }

    @Override
    List<Breakdown> getTrendsFrom(Frequency frequency, Account account = null, int ago = 12) {
        def accounts = account ? [account] : accountService.list()
        (ago..1).collect {
            def startDate = frequencyService.getStartDateForBreakdown(frequency, it)
            def roundedDownStartDate = frequencyService.getRoundedDownStartDate(startDate, frequency)
            def roundedUpEndDate = frequencyService.getRoundedUpEndDate(startDate, frequency)
            getTotalBreakdownFor(frequency, roundedDownStartDate, roundedUpEndDate, accounts, "${roundedUpEndDate.time}")
        }
    }

    private Breakdown getTotalBreakdownFor(Frequency frequency, Date startingDate, Date endingDate, List<Account> accounts, String label) {
        def breakdown = new Breakdown()

        if (accounts.empty) {
            return breakdown
        }

        def accountBudgets = accounts.collectMany(budgetService.&findAllByAccount)
        def allocations = accounts.collectMany(allocationService.&findAllByAccountBetween.rcurry(startingDate, endingDate))

        def totalCredit = accounts.collect(allocationService.&getOverallCreditUpTo.rcurry(endingDate)).sum()
        def totalDebit = accounts.collect(allocationService.&getOverallDebitUpTo.rcurry(endingDate)).collect { it * (-1) }.sum()

        def firstTransactionDate = accounts.collect(transactionService.&getFirstTransactionDateBefore.rcurry(endingDate)).min()
        def lastTransactionDate = accounts.collect(transactionService.&getLastTransactionDateUpTo.rcurry(endingDate)).max()

        return breakdown.with {
            it.label = label
            it.balance = accounts.collect(allocationService.&getAccountBalanceUpTo.rcurry(endingDate)).sum()
            it.totalDebit = calculateTotalDebit(allocations)
            it.totalCredit = calculateTotalCredit(allocations)
            it.allocatedAmount = accountBudgets.collect(this.&getAllocatedAmount.rcurry(frequency)).sum()
            it
        }.with(this.&setRates.curry(frequency, totalCredit, totalDebit, firstTransactionDate, lastTransactionDate))
    }

    private List<Breakdown> getBreakdownBy(Frequency frequency, Date startingDate, Date endingDate, Account account) {
        def accountBudgets = budgetService.findAllByAccount(account)
        def accountAllocations = allocationService.findAllByAccountBetween(account, startingDate, endingDate)

        accountAllocations.groupBy { it.name }
                .collect(this.&createBreakdown.curry(frequency, account, accountBudgets, endingDate))
                .collect(this.&setAllocationRates.curry(frequency, account, endingDate))
    }

    private Breakdown createBreakdown(Frequency frequency, Account account, List<Budget> accountBudgets, Date endingDate, String name, List<Allocation> allocations) {

        def budget = accountBudgets.find { it.name == name }

        new Breakdown(
            account: account,
            budget: budget,
            label: name,
            balance: allocationService.getAccountAllocationBalanceUpTo(account, endingDate, name),
            totalDebit: calculateTotalDebit(allocations),
            totalCredit: calculateTotalCredit(allocations),
            allocatedAmount: getAllocatedAmount(budget, frequency)
        )
    }

    private BigDecimal getAllocatedAmount(Budget budget, Frequency frequency) {
        budget?.with(budgetService.&convert.rcurry(frequency) >> budgetService.&round)?.amount ?: 0
    }

    private Breakdown setAllocationRates(Frequency frequency, Account account, Date endingDate, Breakdown breakdown) {
        def overallCredit = allocationService.getOverallCreditUpTo(account, endingDate, breakdown.label)
        def overallDebit = allocationService.getOverallDebitUpTo(account, endingDate, breakdown.label) * (-1)
        def first = allocationService.getFirstTransactionDateBefore(account, endingDate, breakdown.label)
        def last = allocationService.getlastTransactionDateUpTo(account, endingDate, breakdown.label)
        breakdown.with(this.&setRates.curry(frequency, overallCredit, overallDebit, first, last))
    }

    private Breakdown setRates(Frequency frequency, BigDecimal credit, BigDecimal debit, Date first, Date last, Breakdown breakdown) {
        def duration = frequencyService.getDuration(first, last, frequency)
        def divisor = [1, duration].max()
        breakdown.incomeRate = credit / divisor
        breakdown.expenseRate = debit / divisor
        breakdown
    }

    private BigDecimal calculateTotalCredit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount >= 0 })
    }

    private BigDecimal calculateTotalDebit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount < 0 }) * (-1)
    }
}
