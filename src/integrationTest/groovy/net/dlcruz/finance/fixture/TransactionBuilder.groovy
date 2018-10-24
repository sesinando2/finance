package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.repository.AllocationRepository
import net.dlcruz.finance.dao.repository.TransactionRepository

@Builder(builderStrategy = ExternalStrategy, forClass = Transaction, prefix = 'set', excludes = ['metaClass'])
class TransactionBuilder extends TestDataBuilder<Transaction> {

    AllocationRepository allocationRepository

    @PackageScope
    TransactionBuilder(TransactionRepository service) {
        super(service)

        date = new Date()
        allocations = []
    }

    TransactionBuilder addAllocation(@DelegatesTo(AllocationBuilder) Closure closure) {
        def transaction = persist()
        def builder = new AllocationBuilder(allocationRepository).setTransaction(transaction)
        builder.with(closure)
        builder.persist()
        this
    }
}
