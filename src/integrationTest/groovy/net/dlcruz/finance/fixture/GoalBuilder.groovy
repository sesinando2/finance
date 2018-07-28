package net.dlcruz.finance.fixture

import groovy.time.TimeCategory
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.service.GoalService
import org.codehaus.groovy.runtime.InvokerHelper

class GoalBuilder extends TestDataBuilder<Goal, GoalBuilder> {

    private AccountBuilder accountBuilder

    private GoalService goalService

    private String name
    private Frequency frequency
    private BigDecimal targetAmount
    private Date targetDate

    protected GoalBuilder(Goal entity = null, GoalService goalService, AccountBuilder accountBuilder) {
        super(entity)

        this.goalService = goalService
        this.accountBuilder = accountBuilder

        this.name = "Test Goal ${System.currentTimeMillis()}"
        this.frequency = Frequency.MONTHLY
        this.targetAmount = 5000

        use(TimeCategory) {
            this.targetDate = 6.month.from.now
        }
    }

    @Override
    Goal doBuild() {
        def account = accountBuilder.entity
        def goal = new Goal()
        additionalProperties << [name: name, frequency: frequency, targetAmount: targetAmount, targetDate: targetDate, account: account, amount: targetAmount]
        InvokerHelper.setProperties(goal, additionalProperties)
        goalService.create(goal)
    }

    GoalBuilder setName(String name) {
        this.name = name
        this
    }

    GoalBuilder setFrequency(Frequency frequency) {
        this.frequency = frequency
        this
    }

    GoalBuilder setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount
        this
    }

    GoalBuilder setTargetDate(Date targetDate) {
        this.targetDate = targetDate
        this
    }

    AccountBuilder getAccountBuilder() {
        accountBuilder
    }
}
