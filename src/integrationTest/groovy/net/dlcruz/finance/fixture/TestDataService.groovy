package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestDataService {

    @Autowired
    AccountRepository accountRepository

    @Autowired
    BudgetRepository budgetRepository

    @Autowired
    GoalRepository goalRepository

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    AllocationRepository allocationRepository

    AccountBuilder accountBuilder() {
        def builder = new AccountBuilder(accountRepository)
        builder.setBudgetRepository(budgetRepository)
        builder.setGoalRepository(goalRepository)
        builder.setTransactionRepository(transactionRepository)
        builder.setAllocationRepository(allocationRepository)
        builder
    }

    BudgetBuilder budgetBuilder() {
        new BudgetBuilder(budgetRepository)
    }

    GoalBuilder goalBuilder() {
        new GoalBuilder(goalRepository)
    }

    TransactionBuilder transactionBuilder() {
        def builder = new TransactionBuilder(transactionRepository)
        builder.setAllocationRepository(allocationRepository)
        builder
    }

    AllocationBuilder allocationBuilder() {
        new AllocationBuilder(allocationRepository)
    }
}
