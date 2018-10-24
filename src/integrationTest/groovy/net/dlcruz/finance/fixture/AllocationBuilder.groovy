package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.repository.AllocationRepository

@Builder(builderStrategy = ExternalStrategy, forClass = Allocation, prefix = 'set', excludes = ['metaClass'])
class AllocationBuilder extends TestDataBuilder<Allocation> {

    @PackageScope
    AllocationBuilder(AllocationRepository repository) {
        super(repository)

        name = "Test Allocation ${System.currentTimeMillis()}"
        amount = 200
        transaction = []
    }
}