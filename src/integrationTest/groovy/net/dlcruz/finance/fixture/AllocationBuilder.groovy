package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.service.AllocationService

@Builder(builderStrategy = ExternalStrategy, forClass = Allocation, prefix = 'set', excludes = ['metaClass'])
class AllocationBuilder extends TestDataBuilder<Allocation> {

    @PackageScope
    AllocationBuilder(AllocationService service) {
        super(service)

        name = "Test Allocation ${System.currentTimeMillis()}"
        amount = 200
        transaction = []
    }
}