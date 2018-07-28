package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseIntegrationSpec
import net.dlcruz.finance.fixture.IntegrationTestDataService
import org.joda.time.DateTime
import org.joda.time.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(IntegrationTestConfiguration)
class AllocationRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    AllocationRepository repository

    @Autowired
    IntegrationTestDataService testDataService

    void 'should be able to find all allocations by account'() {
        given:
        def account1 = testDataService.newAccountBuilder().entity
        def transaction = testDataService.newTransactionBuilder(account1)
                .newAllocation().setName('Test Transaction 111').setAmount(100).transactionBuilder
                .newAllocation().setName('Test Transaction 112').setAmount(200).transactionBuilder.entity

        testDataService.newTransactionBuilder(account1)
                .newAllocation().setName('Test Transaction 121').setAmount(300).transactionBuilder
                .newAllocation().setName('Test Transaction 112').setAmount(400).build()

        and:
        def account2 = testDataService.newAccountBuilder().entity
        testDataService.newTransactionBuilder(account2)
                .newAllocation().setName('Test Transaction 211').setAmount(500).transactionBuilder.build()

        when:
        def result = repository.findAllByAccount(account1)

        then:
        result.name.unique() == ['Test Transaction 111', 'Test Transaction 112', 'Test Transaction 121']

        when:
        result = repository.findAllByAccount(account2)

        then:
        result.name == ['Test Transaction 211']

        and:
        repository.getAccountBalance(account1) == 1000
        repository.getAccountBalance(account2) == 500

        and:
        repository.getAllocationBalance(account1, 'Test Transaction 112') == 600

        and:
        repository.getTransactionTotal(transaction) == 300
    }

    void 'should be able to find allocation from a specific date'() {
        given:
        def date = new DateTime() - Period.parse(ago)
        def account = testDataService.newAccountBuilder().entity

        and:
        testDataService.newTransactionBuilder(account).setDate(date.toDate().clearTime())
                .newAllocation().setName('Included Transaction 1').build().transactionBuilder.
        accountBuilder.newTransactionBuilder().setDate(date.plusDays(1).toDate().clearTime())
                .newAllocation().setName('Included Transaction 2').build().transactionBuilder.
        accountBuilder.newTransactionBuilder().setDate(date.minusDays(1).toDate().clearTime())
                .newAllocation().setName('Not Included Transaction').build()

        when:
        def result = repository.findAllByAccountAndDateGreaterThanOrEqualTo(account, date.toDate().clearTime())

        then:
        result.name == ['Included Transaction 1', 'Included Transaction 2']

        where:
        ago << ['P1D', 'P1W', 'P1M', 'P1Y']
    }

    void cleanup() {
        cleanupAccounts()
    }
}
