package net.dlcruz.finance.dao.service

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.repository.AllocationRepository
import net.dlcruz.finance.dao.service.implementation.AllocationEntityService
import net.dlcruz.finance.dao.service.implementation.EntityValidationService
import net.dlcruz.finance.service.MessageResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class AllocationEntitySpec extends Specification {

    @Subject AllocationEntityService service

    @Shared AllocationRepository repository

    void setup() {
        repository = Mock()

        service = new AllocationEntityService(
            repository: repository,
            messageResolver: Mock(MessageResolver),
            validationService: Mock(EntityValidationService)
        )
    }

    void 'when validating a new allocation, one should check for existing allocation for the transaction'() {
        given:
        def transaction = new Transaction()
        def allocation = new Allocation(name: 'New Allocation', transaction: transaction)
        transaction.allocations = [allocation]

        when:
        service.validate(allocation)

        then:
        thrown ObjectValidationException

        and:
        1 * repository.findByNameAndTransaction(allocation.name, transaction) >> new Allocation(id: 1, name: 'New Allocation')
    }

    void 'when validating an existing allocation, one should check for other allocations with the same name for the transaction'() {
        given:
        def transaction = new Transaction()
        def allocation = new Allocation(id: 1, name: 'Existing Allocation', transaction: transaction)
        transaction.allocations = [allocation]

        when:
        service.validate(allocation)

        then:
        thrown ObjectValidationException

        and:
        1 * repository.findByNameAndTransactionAndIdNot(allocation.name, transaction, allocation.id) >> new Allocation(id: 2, name: 'Existing Allocation')
    }
}
