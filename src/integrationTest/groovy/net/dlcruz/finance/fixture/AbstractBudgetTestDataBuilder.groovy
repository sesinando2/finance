package net.dlcruz.finance.fixture

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Budget

@Builder(builderStrategy = ExternalStrategy, forClass = Budget, prefix = 'set', excludes = ['metaClass'])
abstract class AbstractBudgetTestDataBuilder<T extends Budget> extends TestDataBuilder<T> {

}
