package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseIntegrationSpec
import net.dlcruz.finance.dao.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(IntegrationTestConfiguration)
class AllocationRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    AccountService accountService

    @Autowired
    AllocationRepository repository

    void 'should be able to find all allocations by account'() {
        given:
        def account1 = accountBuilder().persist()
        def transaction = transactionBuilder().setAccount(account1)
                .addAllocation { setName('Test Transaction 111').setAmount(100) }
                .addAllocation { setName('Test Transaction 112').setAmount(200) }
                .persist()

        transactionBuilder().setAccount(account1)
                .addAllocation { setName('Test Transaction 121').setAmount(300) }
                .addAllocation { setName('Test Transaction 112').setAmount(400) }

        and:
        def account2 = accountBuilder().addTransaction {
            addAllocation { setName('Test Transaction 211').setAmount(500) }
        }.persist()

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
}
