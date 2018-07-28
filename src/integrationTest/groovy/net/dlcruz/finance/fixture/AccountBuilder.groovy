package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.GoalService
import net.dlcruz.finance.dao.service.TransactionService
import org.codehaus.groovy.runtime.InvokerHelper

class AccountBuilder extends TestDataBuilder<Account, AccountBuilder> {

    private AccountService accountService
    private BudgetService budgetService
    private TransactionService transactionService
    private GoalService goalService

    private String name

    AccountBuilder(Account account,
                   AccountService accountService,
                   BudgetService budgetService,
                   TransactionService transactionService,
                   GoalService goalService) {

        super(account)

        this.accountService = accountService
        this.budgetService = budgetService
        this.transactionService = transactionService
        this.goalService = goalService

        this.name = "Test Account ${System.currentTimeMillis()}"
    }

    AccountBuilder setName(String name) {
        this.name = name
        this
    }

    @Override
    Account doBuild() {
        def account = new Account()
        this.additionalProperties << [name: name]
        InvokerHelper.setProperties(account, additionalProperties)
        this.accountService.create(account)
    }

    BudgetBuilder newBudgetBuilder() {
        new BudgetBuilder(budgetService, this)
    }

    GoalBuilder newGoalBuilder() {
        new GoalBuilder(goalService, this)
    }

    TransactionBuilder newTransactionBuilder() {
        new TransactionBuilder(transactionService, this)
    }
}
