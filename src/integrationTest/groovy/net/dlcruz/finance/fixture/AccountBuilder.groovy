package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.GoalService
import net.dlcruz.finance.dao.service.TransactionService

@Builder(builderStrategy = ExternalStrategy, forClass = Account, prefix = 'set', excludes = ['metaClass'])
class AccountBuilder extends TestDataBuilder<Account> {

    BudgetService budgetService
    GoalService goalService
    TransactionService transactionService
    AllocationService allocationService

    @PackageScope
    AccountBuilder(AccountService service) {
        super(service)

        name = "Test Account ${System.currentTimeMillis()}"
        budgets = []
        transactions = []
    }

    AccountBuilder addBudget(@DelegatesTo(BudgetBuilder) Closure closure) {
        def builder = new BudgetBuilder(budgetService)
        builder.setAccount(entity)
        builder.with(closure)
        builder.persist()
        reload()
        this
    }

    AccountBuilder addGoal(@DelegatesTo(GoalBuilder) Closure closure) {
        def builder = new GoalBuilder(goalService)
        builder.setAccount(entity)
        builder.with(closure)
        builder.persist()
        reload()
        this
    }

    AccountBuilder addTransaction(@DelegatesTo(TransactionBuilder) Closure closure) {
        def entity = persist()
        def builder = new TransactionBuilder(transactionService)
        builder.setAccount(entity)
        builder.setAllocationService(allocationService)
        builder.with(closure)
        builder.persist()
        reload()
        this
    }
}
