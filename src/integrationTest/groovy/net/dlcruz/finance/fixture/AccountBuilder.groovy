package net.dlcruz.finance.fixture

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Account

@Builder(builderStrategy = ExternalStrategy, forClass = Account, prefix = 'set', excludes = ['metaClass'])
class AccountBuilder extends TestDataBuilder<Account> {

    AccountBuilder() {
        name = "Test Account ${System.currentTimeMillis()}"
        budgets = []
        transactions = []
    }
}
