package net.dlcruz.finance.dao.service

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.repository.AccountRepository
import net.dlcruz.finance.dao.service.implementation.AccountEntityService
import net.dlcruz.finance.dao.service.implementation.EntityValidationService
import net.dlcruz.finance.service.MessageResolver
import net.dlcruz.finance.service.SecurityService
import org.springframework.security.authentication.TestingAuthenticationToken
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class AccountEntityServiceSpec extends Specification {

    @Subject AccountEntityService service

    @Shared AccountRepository repository
    @Shared SecurityService securityService
    @Shared AllocationService allocationService

    void setup() {
        repository = Mock()
        securityService = Mock()
        allocationService = Mock()

        service = new AccountEntityService(
            validationService: Mock(EntityValidationService),
            repository: repository,
            securityService: securityService,
            allocationService: allocationService,
            messageResolver: Mock(MessageResolver))

        service.repository.save(_) >> { Account account -> account }
    }

    void 'when creating an account, owner should be set with currently logged in user'() {
        given:
        def authentication = new TestingAuthenticationToken('test_user', null)

        when:
        def account = service.create(new Account(name: 'New Account Name'))

        then:
        account.owner == authentication.principal

        and:
        1 * securityService.getAuthentication() >> authentication
    }

    void 'when validating a new account, one should check for existing account for the current user with the same name'() {
        given:
        def authentication = new TestingAuthenticationToken('test_user', null)
        def newAccount = new Account(name: 'Test Account')

        when:
        service.validate(newAccount)

        then:
        thrown ObjectValidationException

        and:
        1 * securityService.getAuthentication() >> authentication
        1 * repository.findByNameAndOwner(newAccount.name, authentication.principal) >> new Account(name: 'Existing Account')
    }

    void 'when validating an existing account, one should check for other account for the current user with the same name'() {
        given:
        def authentication = new TestingAuthenticationToken('test_user', null)
        def existingAccount = new Account(id: 1, name: 'Existing Account')

        when:
        service.validate(existingAccount)

        then:
        thrown ObjectValidationException

        and:
        1 * service.securityService.getAuthentication() >> authentication
        1 * repository.findByNameAndOwnerAndIdNot(existingAccount.name, authentication.principal, existingAccount.id) >> new Account(id: 2, name: 'Existing Account')
    }
}
