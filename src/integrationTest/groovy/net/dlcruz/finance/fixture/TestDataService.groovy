package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestDataService {

    @Autowired
    AccountService accountService

    @Autowired
    BudgetService budgetService

    @Autowired
    GoalService goalService

    @Autowired
    TransactionService transactionService

    @Autowired
    AllocationService allocationService

    AccountBuilder accountBuilder() {
        def builder = new AccountBuilder(accountService)
        builder.setBudgetService(budgetService)
        builder.setGoalService(goalService)
        builder.setTransactionService(transactionService)
        builder.setAllocationService(allocationService)
        builder
    }

    BudgetBuilder budgetBuilder() {
        new BudgetBuilder(budgetService)
    }

    GoalBuilder goalBuilder() {
        new GoalBuilder(goalService)
    }

    TransactionBuilder transactionBuilder() {
        def builder = new TransactionBuilder(transactionService)
        builder.allocationService = allocationService
        builder
    }

    AllocationBuilder allocationBuilder() {
        new AllocationBuilder(allocationService)
    }
}
