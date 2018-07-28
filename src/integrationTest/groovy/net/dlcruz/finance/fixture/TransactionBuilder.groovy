package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.TransactionService
import org.codehaus.groovy.runtime.InvokerHelper

class TransactionBuilder extends TestDataBuilder<Transaction, TransactionBuilder> {

    private TransactionService transactionService

    private AccountBuilder accountBuilder
    private List<AllocationBuilder> allocations

    private Date date

    TransactionBuilder(TransactionService transactionService,
                       AccountBuilder accountBuilder) {

        super(null)

        this.transactionService = transactionService

        this.accountBuilder = accountBuilder
        this.allocations = []

        date = new Date()
    }

    AllocationBuilder newAllocation() {
        def builder = new AllocationBuilder(null, this)
        allocations << builder
        builder
    }


    @Override
    protected Transaction doBuild() {
        def transaction = new Transaction()
        additionalProperties << [account: accountBuilder.entity, date: date]
        InvokerHelper.setProperties(transaction, additionalProperties)
        transaction.allocations = allocations.collect {
            def allocation = it.doBuild()
            allocation.transaction = transaction
            allocation
        }
        transactionService.create(transaction)
    }

    AccountBuilder getAccountBuilder() {
        accountBuilder
    }

    TransactionBuilder setDate(Date date) {
        this.date = date
        this
    }
}
