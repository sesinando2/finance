package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Allocation
import org.codehaus.groovy.runtime.InvokerHelper

class AllocationBuilder extends TestDataBuilder<Allocation, AllocationBuilder> {

    private TransactionBuilder transactionBuilder

    private String name
    private BigDecimal amount

    protected AllocationBuilder(Allocation entity = null, TransactionBuilder transactionBuilder) {
        super(entity)

        this.transactionBuilder = transactionBuilder

        this.name = "Test Allocation ${System.currentTimeMillis()}"
        this.amount = 200
    }

    AllocationBuilder setName(String name) {
        this.name = name
        this
    }

    AllocationBuilder setAmount(BigDecimal amount) {
        this.amount = amount
        this
    }

    @Override
    Allocation doBuild() {
        def allocation = new Allocation()
        additionalProperties << [name: name, amount: amount]
        InvokerHelper.setProperties(allocation, additionalProperties)
        allocation
    }

    @Override
    AllocationBuilder build() {
        transactionBuilder.build()
        this
    }

    TransactionBuilder getTransactionBuilder() {
        transactionBuilder
    }
}