package net.dlcruz.finance.controller

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseControllerSpec
import net.dlcruz.finance.dao.service.implementation.AccountEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus

import static net.dlcruz.finance.dao.domain.Frequency.WEEKLY
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class BreakdownControllerSpec extends BaseControllerSpec {

    @Autowired
    AccountEntityService accountService

    void 'should return all accounts breakdown'() {
        given:
        def account = accountBuilder().setName('Test Breakdown Account 1').setOwner('another_user').update()

        def budget = budgetBuilder().setAccount(account).setName('Test Budget 1').setAmount(100).setFrequency(WEEKLY).persist()
        transactionBuilder().setAccount(account)
                .addAllocation { setName(budget.name).setAmount(100) }
                .addAllocation { setName('Test Budget 3').setAmount(200) }
                .persist()

        and:
        def anotherAccount = accountBuilder().setName('Test Breakdown Account 2').setOwner('another_user').update()
        def anotherBudget = budgetBuilder().setAccount(anotherAccount).setName('Test Budget 2').setAmount(200).setFrequency(WEEKLY).persist()

        and:
        transactionBuilder().setAccount(anotherAccount).addAllocation {
            setName(anotherBudget.name).setAmount(200)
        }

        transactionBuilder().setAccount(anotherAccount).addAllocation {
            setName(anotherBudget.name).setAmount(-50)
        }

        transactionBuilder().setAccount(anotherAccount).addAllocation {
            setName(anotherBudget.name).setAmount(-25)
        }

        transactionBuilder().setAccount(anotherAccount).addAllocation {
            setName(anotherBudget.name).setAmount(50)
        }

        when:
        setUser('another_user')
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
    }
}
