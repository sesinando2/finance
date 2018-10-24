package net.dlcruz.finance.fixture

import groovy.time.TimeCategory
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.repository.BudgetRepository
import net.dlcruz.finance.dao.repository.GoalRepository
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.GoalService

@Builder(builderStrategy = ExternalStrategy, forClass = Goal, prefix = 'set', excludes = ['metaClass'])
class GoalBuilder extends AbstractBudgetTestDataBuilder<Goal> {

    static GoalBuilder from(Account account) {
        new GoalBuilder().setAccount(account)
    }

    GoalBuilder() {
        name = "Test Goal ${System.currentTimeMillis()}"
        frequency = Frequency.MONTHLY
        amount = 0
        targetAmount = 5000

        use(TimeCategory) {
            targetDate = 6.month.from.now
        }
    }

    Goal persist(GoalService service) {
        service.create(build())
    }

    Goal persist(GoalRepository repository) {
        repository.save(build())
    }
}
