package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.repository.*

@Builder(builderStrategy = ExternalStrategy, forClass = Account, prefix = 'set', excludes = ['metaClass'])
class AccountBuilder extends TestDataBuilder<Account> {

    BudgetRepository budgetRepository
    GoalRepository goalRepository
    TransactionRepository transactionRepository
    AllocationRepository allocationRepository

    @PackageScope
    AccountBuilder(AccountRepository repository) {
        super(repository)

        name = "Test Account ${System.currentTimeMillis()}"
        budgets = []
        transactions = []
    }

    AccountBuilder addBudget(@DelegatesTo(BudgetBuilder) Closure closure) {
        def builder = new BudgetBuilder(budgetRepository)
        builder.setAccount(entity)
        builder.with(closure)
        builder.persist()
        this
    }

    AccountBuilder addGoal(@DelegatesTo(GoalBuilder) Closure closure) {
        def builder = new GoalBuilder(goalRepository)
        builder.setAccount(entity)
        builder.with(closure)
        builder.persist()
        this
    }

    AccountBuilder addTransaction(@DelegatesTo(TransactionBuilder) Closure closure) {
        def entity = persist()
        def builder = new TransactionBuilder(transactionRepository)
        builder.setAccount(entity)
        builder.setAllocationRepository(allocationRepository)
        builder.with(closure)
        builder.persist()
        this
    }
}
