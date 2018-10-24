package net.dlcruz.finance.controller

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseControllerSpec
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
import spock.lang.Stepwise

import static net.dlcruz.finance.dao.domain.Frequency.WEEKLY
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Stepwise
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class BreakdownControllerSpec extends BaseControllerSpec {

    @Autowired
    AccountService accountService

    void 'should return all accounts breakdown'() {
        given:
        def account = new AccountBuilder().setName('Test Breakdown Account 1').persist(accountService)
        def budget = BudgetBuilder.from(account).setName('Test Budget 1').setAmount(100).setFrequency(WEEKLY).persist(budgetRepository)
        def transaction = TransactionBuilder.from(account).persist(transactionRepository)

        and:
        AllocationBuilder.from(transaction).setName(budget.name).setAmount(100).persist(allocationRepository)
        AllocationBuilder.from(transaction).setName('Test Budget 3').setAmount(200).persist(allocationRepository)

        and:
        def anotherAccount = new AccountBuilder().setName('Test Breakdown Account 2').persist(accountService)
        def anotherBudget = BudgetBuilder.from(anotherAccount).setName('Test Budget 2').setAmount(200).setFrequency(WEEKLY).persist(budgetRepository)

        and:
        TransactionBuilder.from(anotherAccount).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName(anotherBudget.name).setAmount(200).persist(allocationRepository)
        }

        TransactionBuilder.from(anotherAccount).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName(anotherBudget.name).setAmount(-50).persist(allocationRepository)
        }

        TransactionBuilder.from(anotherAccount).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName(anotherBudget.name).setAmount(-25).persist(allocationRepository)
        }

        TransactionBuilder.from(anotherAccount).persist(transactionRepository).with {
            AllocationBuilder.from(it).setName(anotherBudget.name).setAmount(50).persist(allocationRepository)
        }

        when:
        def response = restTemplate.getForEntity('/weekly-breakdown', List)
        List<Map> breakdownList = response.body

        then:
        response.statusCode == HttpStatus.OK

        and:
        breakdownList.size() == 3
        breakdownList*.label.containsAll(['Test Budget 1', 'Test Budget 2', 'Test Budget 3'])

        when:
        def budget1 = breakdownList.find { it.label == 'Test Budget 1' }
        def budget2 = breakdownList.find { it.label == 'Test Budget 2' }
        def budget3 = breakdownList.find { it.label == 'Test Budget 3' }

        then:
        budget1.account.name == 'Test Breakdown Account 1'
        budget1.budget.name == 'Test Budget 1'
        budget1.balance == 100
        budget1.totalCredit == 100
        budget1.totalDebit == 0
        budget1.allocatedAmount == 100

        and:
        budget2.account.name == 'Test Breakdown Account 2'
        budget2.budget.name == 'Test Budget 2'
        budget2.balance == 175
        budget2.totalCredit == 250
        budget2.totalDebit == 75
        budget2.allocatedAmount == 200

        and:
        budget3.account.name == 'Test Breakdown Account 1'
        budget3.budget == null
        budget3.balance == 200
        budget3.totalCredit == 200
        budget3.totalDebit == 0
        budget3.allocatedAmount == 0

        cleanup:
        cleanupAccounts()
    }
}
