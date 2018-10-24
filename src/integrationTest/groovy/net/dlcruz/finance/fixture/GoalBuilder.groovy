package net.dlcruz.finance.fixture

import groovy.time.TimeCategory
import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.repository.GoalRepository

@Builder(builderStrategy = ExternalStrategy, forClass = Goal, prefix = 'set', excludes = ['metaClass'])
class GoalBuilder extends TestDataBuilder<Goal> {

    @PackageScope
    GoalBuilder(GoalRepository repository) {
        super(repository)

        name = "Test Goal ${System.currentTimeMillis()}"
        frequency = Frequency.MONTHLY
        amount = 0
        targetAmount = 5000

        use(TimeCategory) {
            targetDate = 6.month.from.now
        }
    }
}
