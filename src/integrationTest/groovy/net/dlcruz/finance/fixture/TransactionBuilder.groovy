package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.TransactionService

@Builder(builderStrategy = ExternalStrategy, forClass = Transaction, prefix = 'set', excludes = ['metaClass'])
class TransactionBuilder extends TestDataBuilder<Transaction> {

    AllocationService allocationService

    @PackageScope
    TransactionBuilder(TransactionService service) {
        super(service)

        date = new Date()
        allocations = []
    }

    TransactionBuilder addAllocation(@DelegatesTo(AllocationBuilder) Closure closure) {
        def builder = new AllocationBuilder(allocationService).setTransaction(entity)
        builder.with(closure)
        builder.persist()
        reload()
        this
    }
}
