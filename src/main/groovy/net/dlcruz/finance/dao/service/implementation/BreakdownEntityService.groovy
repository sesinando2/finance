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

    @Override
    List<Breakdown> getBreakdown(Frequency frequency, Account account = null) {
        def accounts = account ? [account] : accountService.list()
        accounts.collectMany(this.&getBreakdownBy.curry(frequency))
    }

    private List<Breakdown> getBreakdownBy(Frequency frequency, Account account) {
        def accountBudgets = budgetService.findAllByAccount(account)
        def startingDate = getStartingDateForBreakdown(frequency)
        def accountAllocations = allocationService.findAllByAccountStartingFrom(account, startingDate)
        accountAllocations.groupBy { it.name }.collect(this.&createBreakdown.curry(frequency, account, accountBudgets))
    }

    private Breakdown createBreakdown(Frequency frequency, Account account, List<Budget> accountBudgets,
                                      String name, List<Allocation> allocations) {

        def budget = accountBudgets.find { it.name == name }

        new Breakdown(
                account: account,
                budget: budget,
                label: name,
                balance: allocationService.getAllocationBalance(account, name),
                totalDebit:  allocationService.sum(allocations.findAll { it.amount < 0 }) * (-1),
                totalCredit: allocationService.sum(allocations.findAll { it.amount >= 0 }),
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
}
