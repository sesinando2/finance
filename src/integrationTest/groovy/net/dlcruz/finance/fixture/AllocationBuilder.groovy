package net.dlcruz.finance.fixture

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Transaction

@Builder(builderStrategy = ExternalStrategy, forClass = Allocation, prefix = 'set', excludes = ['metaClass'])
class AllocationBuilder extends TestDataBuilder<Allocation> {

    static AllocationBuilder from(Transaction transaction) {
        new AllocationBuilder().setTransaction(transaction)
    }

    AllocationBuilder() {
        name = "Test Allocation ${System.currentTimeMillis()}"
        amount = 200
        transaction = []
    }
}