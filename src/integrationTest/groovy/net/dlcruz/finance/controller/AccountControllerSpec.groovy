package net.dlcruz.finance.controller

import groovy.time.TimeCategory
import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseControllerSpec
import net.dlcruz.finance.dao.domain.*
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

import static groovy.time.TimeCategory.*
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Unroll
@Stepwise
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class AccountControllerSpec extends BaseControllerSpec {

    @Autowired
    AccountService accountService

    @Autowired
    AllocationService allocationService

    @Autowired
    TransactionService transactionService

    @Shared Account account
    @Shared Budget budget
    @Shared Goal goal
    @Shared Transaction transaction
    @Shared Allocation allocation

    void 'when creating an account, should not allow user to explicitly set the account owner'() {
        when:
        def response = restTemplate.postForEntity('/account',
                new Account(name: "Test Account ${System.currentTimeMillis()}", owner: 'set_owner'), Account)

        and:
        account = accountService.get(response.body.id)

        then:
        response.statusCode == HttpStatus.OK

        and:
        account.owner == TEST_USER

        cleanup:
        account = response.body
    }

    void 'when updating an account, should not allow user to explicitly set the account owner'() {
        given:
        def UPDATED_ACCOUNT_NAME = "Update Account ${System.currentTimeMillis()}".toString()

        when:
        restTemplate.put('/account/{id}', [name: UPDATED_ACCOUNT_NAME, owner: 'set_owner'], account.id)

        and:
        account = accountService.get(account.id)

        then:
        account.owner == TEST_USER
        account.name == UPDATED_ACCOUNT_NAME
    }

    void 'should be able to create a budget for an account'() {
        given:
        budget = new Budget(name: 'Test Budget', amount: 500, frequency: Frequency.WEEKLY)

        when:
        def response = restTemplate.postForEntity('/account/{id}/budget', budget, Budget, account.id)

        then:
        response.statusCode == HttpStatus.OK

        when:
        def budgetResponse = response.body

        then:
        budgetResponse.name == budget.name
        budgetResponse.amount == budget.amount
        budgetResponse.frequency == budget.frequency

        cleanup:
        budget = budgetResponse
    }

    void 'should be able to get the list of budget for an account'() {
        when:
        def response = restTemplate.getForEntity("/account/{id}/${frequency.name().toLowerCase()}-budget", List, account.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 1

        when:
        Map properties = response.body[0]
        properties.remove('type')
        def budgetResponse = new Budget(properties)

        then:
        budgetResponse.name == budget.name
        budgetResponse.amount == amount
        budgetResponse.frequency == frequency

        where:
        frequency           | amount
        Frequency.ANNUALLY  | 26072
        Frequency.MONTHLY   | 2173
        Frequency.WEEKLY    | 500
        Frequency.DAILY     | 72
    }

    void 'should be able to create a goal for an account'() {
        given:
        def targetDate = TimeCategory.getMonths(5).from.now
        goal = new Goal(name: 'Test Goal', targetAmount: 5000, targetDate: targetDate, frequency: Frequency.MONTHLY)

        when:
        def response = restTemplate.postForEntity('/account/{id}/goal', goal, Goal, account.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body.amount == 1000

        cleanup:
        goal = response.body
    }

    void 'should be able to include goal from list of budget for an account'() {
        when:
        def response = restTemplate.getForEntity("/account/{id}/budget", List, account.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body*.id == [budget.id, goal.id]
        response.body*.type == ['Budget', 'Goal']
    }

    void 'should be able to create a transaction for an account'() {
        given:
        allocation = new Allocation(name: 'Rent', amount: 500)
        transaction = new Transaction(description: 'This is a test transaction', allocations: [allocation])

        when:
        def response = restTemplate.postForEntity('/account/{id}/transaction', transaction, Transaction, account.id)

        then:
        response.statusCode == HttpStatus.OK

        when:
        def transactionResponse = response.body

        then:
        transactionResponse.description == transaction.description

        when:
        def allocationResponse = transaction.allocations[0]

        then:
        allocationResponse.name == allocation.name
        allocationResponse.amount == allocation.amount

        cleanup:
        allocation = allocationResponse
        transaction = transactionResponse
    }

    void 'should be able to get the list of transactions'() {
        given:
        transaction = transactionService.get(transaction.id)

        when:
        def response = restTemplate.getForEntity('/account/{id}/transaction', List, account.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 1

        when:
        def transactionResponse = response.body[0]

        then:
        transactionResponse.id == transaction.id
        transactionResponse.description == transaction.description
        transactionResponse.allocations.size() == 1
        transactionResponse.total == allocationService.findAllByTransaction(transaction).amount.sum()
    }

    void 'should be able to get the list of paginated results'() {
        given:
        def transaction = testDataService.newTransactionBuilder(account)
                .newAllocation().setName('Test Allocation 1').setAmount(100)
                .transactionBuilder.build().entity

        when:
        def response = restTemplate.getForEntity("/account/{id}/transaction/page?page=$page&size=$size", Map, account.id)

        then:
        response.statusCode == HttpStatus.OK

        when:
        def pageResponse = response.body as Map

        then:
        pageResponse.totalPages == totalPages
        pageResponse.totalElements == totalElements
        pageResponse.first == first
        pageResponse.last == last

        cleanup:
        transactionService.delete(transaction)

        where:
        page | size | totalPages | totalElements | first | last
        0    | 2    | 1          | 2             | true  | true
        0    | 1    | 2          | 2             | true  | false
        1    | 1    | 2          | 2             | false | true
    }

    void 'should be able to calculate the account balance correctly'() {
        given:
        transaction = testDataService.newTransactionBuilder(account)
                .newAllocation().setName('Allocation 1').setAmount(200)
                .transactionBuilder.newAllocation().setName('Allocation 2').setAmount(-300)
                .transactionBuilder.build().entity

        when:
        def response = restTemplate.getForEntity('/account/{id}', Account, account.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body.balance == 400
    }

    void 'should be able to calculate account breakdown'() {
        given:
        transaction = testDataService
                .newTransactionBuilder(account).setDate(date)
                .newAllocation().setName(budget.name).setAmount(amount)
                .transactionBuilder.build().entity

        when:
        def response = restTemplate.getForEntity("/account/{id}/$frequency-breakdown", List, account.id)

        then:
        response.statusCode == HttpStatus.OK

        when:
        List<Map> breakdownList = response.body
        def breakdown = breakdownList.find { it.label == budget.name }

        then:
        breakdown.account.id == account.id
        breakdown.budget.id == budget.id
        breakdown.label == budget.name
        breakdown.balance == balance
        breakdown.totalDebit == totalDebit
        breakdown.totalCredit == totalCredit
        breakdown.allocatedAmount == allocatedAmount

        where:
        frequency   | balance   | totalDebit    | totalCredit   | allocatedAmount | amount | date
        'annually'  | 250       | 0             | 250           | 26072           | 250    | getMonths(3).ago
        'annually'  | 0         | 250           | 250           | 26072           | -250   | getMonths(2).ago
        'monthly'   | 2173      | 0             | 2173          | 2173            | 2173   | getWeeks(2).ago
        'weekly'    | 1673      | 500           | 0             | 500             | -500   | getDays(2).ago
        'daily'     | 1203      | 470           | 0             | 72              | -470   | new Date()
    }

    void 'should be able to delete an account'() {
        when:
        restTemplate.delete('/account/{id}', account.id)

        and:
        def existingAccount = accountService.get(account.id)

        then:
        existingAccount == null

        cleanup:
        cleanupAccounts()
    }
}
