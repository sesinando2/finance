package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.GoalService
import net.dlcruz.finance.dao.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IntegrationTestDataService {

    @Autowired
    private AccountService accountService

    @Autowired
    private BudgetService budgetService

    @Autowired
    private GoalService goalService

    @Autowired
    private TransactionService transactionService

    AccountBuilder newAccountBuilder(Account account = null) {
        new AccountBuilder(account, accountService, budgetService, transactionService, goalService)
    }

    BudgetBuilder newBudgetBuilder(Account account = null) {
        newAccountBuilder(account).newBudgetBuilder()
    }

    GoalBuilder newGoalBuilder(Account account = null) {
        newAccountBuilder(account).newGoalBuilder()
    }

    TransactionBuilder newTransactionBuilder(Account account = null) {
        newAccountBuilder(account).newTransactionBuilder()
    }
}