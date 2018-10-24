package net.dlcruz.finance.fixture

import groovy.transform.PackageScope
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.service.BudgetService

import static net.dlcruz.finance.dao.domain.Frequency.MONTHLY

@Builder(builderStrategy = ExternalStrategy, forClass = Budget, prefix = 'set', excludes = ['metaClass'])
class BudgetBuilder extends TestDataBuilder<Budget> {

    @PackageScope
    BudgetBuilder(BudgetService service) {
        super(service)

        name = "Test Budget ${System.currentTimeMillis()}"
        amount = 500
        frequency = MONTHLY
    }
}