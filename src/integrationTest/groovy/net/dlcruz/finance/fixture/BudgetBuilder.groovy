package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.service.BudgetService
import org.codehaus.groovy.runtime.InvokerHelper

class BudgetBuilder extends TestDataBuilder<Budget, BudgetBuilder> {

    private BudgetService budgetService

    private AccountBuilder accountBuilder

    private String name
    private BigDecimal amount
    private Frequency frequency

    BudgetBuilder(Budget budget = null,
                  BudgetService budgetService,
                  AccountBuilder accountBuilder) {

        super(budget)

        this.budgetService = budgetService
        this.accountBuilder = accountBuilder

        this.name = "Test Budget ${System.currentTimeMillis()}"
        this.amount = 500
        this.frequency = Frequency.MONTHLY
    }

    BudgetBuilder setName(String name) {
        this.name = name
        this
    }

    BudgetBuilder setAmount(BigDecimal amount) {
        this.amount = amount
        this
    }

    BudgetBuilder setFrequency(Frequency frequency) {
        this.frequency = frequency
        this
    }

    @Override
    Budget doBuild() {
        def account = accountBuilder.entity
        def budget = new Budget()
        additionalProperties << [name: this.name, amount: this.amount, frequency: this.frequency, account: account]
        InvokerHelper.setProperties(budget, additionalProperties)
        this.budgetService.create(budget)
    }

    AccountBuilder getAccountBuilder() {
        accountBuilder
    }
}