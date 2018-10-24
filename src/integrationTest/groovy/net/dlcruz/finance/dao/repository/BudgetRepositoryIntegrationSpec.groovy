package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseIntegrationSpec
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

import static groovy.time.TimeCategory.getDay
import static groovy.time.TimeCategory.getMonths

@SpringBootTest
@Import(IntegrationTestConfiguration)
class BudgetRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    AccountService accountService

    @Autowired
    BudgetRepository repository

    void 'should not include completed goals'() {
        given:
        def account = accountBuilder().persist()

        and:
        def budget = budgetBuilder().setAccount(account).persist()
        def goal = goalBuilder().setAccount(account).setTargetDate(new Date()).persist()

        and:
        def goal2 = goalBuilder().setAccount(account).setTargetDate(getMonths(6).ago).persist()
        def goal3 = goalBuilder().setAccount(account).setTargetDate(getDay(1).ago).persist()

        and:
        def completedGoal = goalBuilder().setAccount(account).persist() as Goal
        transactionBuilder().setAccount(account).addAllocation {
            setName(completedGoal.name).setAmount(5000)
        }

        when:
        def results = repository.findAllByAccount(account)

        then:
        results*.id == [budget.id, goal.id, goal2.id, goal3.id]
    }
}
