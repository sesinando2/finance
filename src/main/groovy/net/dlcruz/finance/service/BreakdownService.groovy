package net.dlcruz.finance.service

import net.dlcruz.finance.api.model.Breakdown
import net.dlcruz.finance.dao.domain.*
import net.dlcruz.finance.dao.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BreakdownService {

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

    Breakdown getTotalBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        def label = account ? account.name : 'All Accounts'
        def start = frequencyService.getStartDateForBreakdown(frequency)
        getTotalBreakdownFor(frequency, start,  new Date(), accounts, label)
    }

    List<Breakdown> getBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        def start = frequencyService.getStartDateForBreakdown(frequency)
        accounts.collectMany(this.&getBreakdownBy.curry(frequency, start, new Date()))
    }

    List<Breakdown> getTrendsFrom(Frequency frequency, Account account = null, int ago = 12) {
        def accounts = account ? [account] : accountService.list()
        (ago..0).collect {
            def startDate = frequencyService.getStartDateForBreakdown(frequency, it)
            def roundedDownStartDate = frequencyService.getRoundedDownStartDate(startDate, frequency)
            def endDate = frequencyService.getEndDateForBreakdown(frequency, roundedDownStartDate)
            getTotalBreakdownFor(frequency, roundedDownStartDate, endDate, accounts, "${endDate.time}")
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
        def allocationNames = allocationService.findAllNamesByAccount(account)
        def labels = (accountBudgets*.name + allocationNames).unique()

        labels
                .collect(this.&createBreakdown.curry(frequency, account, accountBudgets, startingDate, endingDate))
                .collect(this.&setAllocationRates.curry(frequency, account, endingDate))
    }

    private Breakdown createBreakdown(Frequency frequency, Account account, List<Budget> accountBudgets, Date startingDate, Date endingDate, String name) {
        def budget = accountBudgets.find { it.name == name }

        new Breakdown(
            account: account,
            budget: budget,
            label: name,
            balance: allocationService.getAccountAllocationBalanceUpTo(account, endingDate, name),
            totalDebit: allocationService.getAccountAllocationOverAllDebitBetween(account, name, startingDate, endingDate),
            totalCredit: allocationService.getAccountAllocationOverAllCreditBetween(account, name, startingDate, endingDate),
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
        def startDate = frequencyService.getRoundedDownStartDate(first, frequency)
        def endDate = frequencyService.getRoundedUpEndDate(last, frequency) + 1
        def duration = frequencyService.getDuration(startDate, endDate, frequency)
        def periodOfTime = [1, duration].max()
        breakdown.incomeRate = calculateRate(credit, periodOfTime)
        breakdown.expenseRate = calculateRate(debit, periodOfTime)
        breakdown
    }

    private BigDecimal calculateTotalCredit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount >= 0 })
    }

    private BigDecimal calculateTotalDebit(List<Allocation> allocations) {
        allocationService.sum(allocations.findAll { it.amount < 0 }) * (-1)
    }

    private BigDecimal calculateRate(BigDecimal amount, int periodOfTime) {
        amount / periodOfTime
    }
}
