package net.dlcruz.finance.dao.service

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.repository.BudgetRepository
import net.dlcruz.finance.dao.service.implementation.BudgetEntityService
import net.dlcruz.finance.dao.service.implementation.EntityValidationService
import net.dlcruz.finance.service.MessageResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class BudgetEntityServiceSpec extends Specification {

    @Subject BudgetEntityService service

    @Shared BudgetRepository repository

    void setup() {
        repository = Mock()

        service = new BudgetEntityService(
            repository: repository,
            messageResolver: Mock(MessageResolver),
            validationService: Mock(EntityValidationService)
        )
    }

    void 'when updating a new budget, one should check for existing budget for the account'() {
        given:
        def account = new Account()
        def budget = new Budget(name: 'New Budget', account: account)
        account.budgets = [budget]

        when:
        service.validate(budget)

        then:
        thrown ObjectValidationException

        and:
        1 * repository.findByNameAndAccount(budget.name, account) >> new Budget(id: 1, name: 'New Budget')
    }

    void 'when updating an existing budget, one should check for other budget with the same name for the account'() {
        given:
        def account = new Account()
        def budget = new Budget(id: 1, name: 'Existing Budget', account: account)
        account.budgets = [budget]

        when:
        service.validate(budget)

        then:
        thrown ObjectValidationException

        and:
        1 * repository.findByNameAndAccountAndIdNot(budget.name, account, budget.id) >> new Budget(id: 2, name: 'Existing Budget')
    }
}
