package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseIntegrationSpec
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.fixture.AccountBuilder
import net.dlcruz.finance.fixture.AllocationBuilder
import net.dlcruz.finance.fixture.BudgetBuilder
import net.dlcruz.finance.fixture.GoalBuilder
import net.dlcruz.finance.fixture.TransactionBuilder
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
        def account = new AccountBuilder().persist(accountService)

        and:
        def budget = BudgetBuilder.from(account).persist(repository)
        def goal = GoalBuilder.from(account).setTargetDate(new Date()).persist(goalRepository)

        and:
        def goal2 = GoalBuilder.from(account).setTargetDate(getMonths(6).ago).persist(goalRepository)
        def goal3 = GoalBuilder.from(account).setTargetDate(getDay(1).ago).persist(goalRepository)

        and:
        def completedGoal = GoalBuilder.from(account).persist(goalRepository)
        TransactionBuilder.from(account).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName(completedGoal.name).setAmount(5000).persist(allocationRepository)
        }

        when:
        def results = repository.findAllByAccount(account)

        then:
        results*.id == [budget.id, goal.id, goal2.id, goal3.id]
    }

    void cleanup() {
        cleanupAccounts()
    }
}
