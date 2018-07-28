package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

import static groovy.time.TimeCategory.getDay
import static groovy.time.TimeCategory.getMonths

@SpringBootTest
@Import(IntegrationTestConfiguration)
class BudgetRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    BudgetRepository repository

    void 'should not include completed goals'() {
        given:
        def account = testDataService.newAccountBuilder()

        and:
        def budget = account.newBudgetBuilder().entity
        def goal = account.newGoalBuilder().setTargetDate(new Date()).entity

        and:
        def goal2 = account.newGoalBuilder().setTargetDate(getMonths(6).ago).entity
        def goal3 = account.newGoalBuilder().setTargetDate(getDay(1).ago).entity

        and:
        def completedGoal = account.newGoalBuilder().entity
        account.newTransactionBuilder()
                .newAllocation().setName(completedGoal.name).setAmount(5000).build()

        when:
        def results = repository.findAllByAccount(account.entity)

        then:
        results*.id == [budget.id, goal.id, goal2.id, goal3.id]
    }
}
