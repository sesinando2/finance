package net.dlcruz.finance.controller

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseControllerSpec
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.fixture.AccountBuilder
import net.dlcruz.finance.fixture.AllocationBuilder
import net.dlcruz.finance.fixture.BudgetBuilder
import net.dlcruz.finance.fixture.TransactionBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Stepwise

import static net.dlcruz.finance.dao.domain.Frequency.WEEKLY
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Stepwise
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class BudgetControllerSpec extends BaseControllerSpec {

    @Autowired
    AccountService accountService

    @Autowired
    BudgetService budgetService

    @Shared Account account

    void 'test post budget should return not supported'() {
        given:
        account = new AccountBuilder().setName('Test Account').persist(accountService)
        def budget = new Budget(name: 'Test Budget', amount: 100, frequency: WEEKLY, account: account)

        when:
        def response = restTemplate.postForEntity('/budget', budget, Budget)

        then:
        response.statusCode == HttpStatus.METHOD_NOT_ALLOWED
    }

    void 'test get budget'() {
        given:
        def budget = BudgetBuilder.from(account).setName('Test Budget').setAmount(100).setFrequency(WEEKLY).persist(budgetService)

        when:
        def response = restTemplate.getForEntity('/budget/{id}', Budget, budget.id)

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.name == 'Test Budget'
        response.body.amount == 100
        response.body.frequency == WEEKLY

        cleanup:
        budgetService.delete(budget)
    }

    void 'test update budget'() {
        given:
        def budget = BudgetBuilder.from(account).setName('Test Budget').setAmount(100).setFrequency(WEEKLY).persist(budgetService)

        when:
        def properties = [name: 'Updated Budget Name']
        restTemplate.put('/budget/{id}', properties, budget.id)

        then:
        budgetService.get(budget.id).name == 'Updated Budget Name'
    }

    void 'test delete budget'() {
        given:
        def budget = BudgetBuilder.from(account).setName('Test Budget').setAmount(100).setFrequency(WEEKLY).persist(budgetService)

        when:
        restTemplate.delete('/budget/{id}', budget.id)

        then:
        budgetService.get(budget.id) == null
    }

    void 'test budget balance is correctly calculated'() {
        given:
        def budget = BudgetBuilder.from(account).setName('Test Budget').setAmount(100).setFrequency(WEEKLY).persist(budgetService)

        and:
        TransactionBuilder.from(account).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName('Test Budget').setAmount(100).persist(allocationRepository)
            AllocationBuilder.from(it).setName('Another Budget').setAmount(200).persist(allocationRepository)
        }

        when:
        def response = restTemplate.getForEntity('/budget/{id}', Budget, budget.id)

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.balance == 100

        when:
        TransactionBuilder.from(account).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName('Test Budget').setAmount(-25).persist(allocationRepository)
        }

        and:
        response = restTemplate.getForEntity('/budget/{id}', Budget, budget.id)

        then:
        response.statusCode == HttpStatus.OK

        and:
        response.body.balance == 75

        cleanup:
        cleanupAccounts()
    }
}
