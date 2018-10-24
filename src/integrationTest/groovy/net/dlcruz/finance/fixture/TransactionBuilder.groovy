package net.dlcruz.finance.fixture

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Transaction

@Builder(builderStrategy = ExternalStrategy, forClass = Transaction, prefix = 'set', excludes = ['metaClass'])
class TransactionBuilder extends TestDataBuilder<Transaction> {

    static TransactionBuilder from(Account account) {
        new TransactionBuilder().setAccount(account)
    }

    TransactionBuilder() {
        date = new Date()
        allocations = []
    }
}
