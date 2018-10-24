package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.repository.BudgetRepository
import net.dlcruz.finance.dao.service.BudgetService

import static net.dlcruz.finance.dao.domain.Frequency.MONTHLY

class BudgetBuilder extends AbstractBudgetTestDataBuilder<Budget> {

    static BudgetBuilder from(Account account) {
        new BudgetBuilder().setAccount(account)
    }

    BudgetBuilder() {
        name = "Test Budget ${System.currentTimeMillis()}"
        amount = 500
        frequency = MONTHLY
    }

    Budget persist(BudgetService service) {
        service.create(build())
    }

    Budget persist(BudgetRepository repository) {
        repository.save(build())
    }
}