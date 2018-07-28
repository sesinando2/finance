package net.dlcruz.finance.dao.service.implementation

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.repository.AccountRepository
import net.dlcruz.finance.dao.service.AccountService
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.base.BaseEntityService
import net.dlcruz.finance.service.MessageResolver
import net.dlcruz.finance.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
class AccountEntityService extends BaseEntityService<Account, Long> implements AccountService {

    @Autowired
    EntityValidationService validationService

    @Autowired
    AccountRepository repository

    @Autowired
    SecurityService securityService

    @Autowired
    MessageResolver messageResolver

    @Autowired
    BudgetService budgetService

    @Autowired
    AllocationService allocationService

    @Override
    boolean exist(Account account) {
        getExistingUserAccount(account)
    }

    @Override
    @Transactional
    List<Account> list() {
        repository.findAllByOwnerOrderByNameAsc(securityService.authentication.principal)
    }

    @Override
    Page<Account> listByPage(Pageable pageable) {
        repository.findAllByOwnerOrderByNameAsc(securityService.authentication.principal, pageable)
    }

    @Override
    @Transactional
    Account create(Account account) {
        account.owner = securityService.authentication.principal
        super.create(account)
    }

    @Override
    ObjectValidationException validate(Account account) {
        def validationException = doJpaValidation(account)
        validateUniqueAccountForUser(account, validationException)

        if (validationException.hasError) {
            throw validationException
        }

        validationException
    }

    private List<Account> findAllAccount(Pageable pageable) {
        if (pageable) {
            repository.findAllByOwnerOrderByNameAsc(securityService.authentication.principal, pageable)
        } else {
            repository.findAllByOwnerOrderByNameAsc(securityService.authentication.principal)
        }
    }

    private void validateUniqueAccountForUser(Account account, ObjectValidationException validationException) {
        def existingAccount = getExistingUserAccount(account)

        if (existingAccount) {
            def fieldName = 'name'
            def defaultMessage = messageResolver.getMessage(ObjectValidationException.UNIQUE_CONSTRAINT, [fieldName])
            validationException.pushUniqueConstraint(account, fieldName, existingAccount, defaultMessage)
        }
    }

    private ObjectValidationException doJpaValidation(Account account) {
        try {
            return super.validate(account)
        } catch (ObjectValidationException ex) {
            return ex
        }
    }

    private Account getExistingUserAccount(Account account) {
        if (account.id == null) {
            repository.findByNameAndOwner(account.name, securityService.authentication.principal)
        } else {
            repository.findByNameAndOwnerAndIdNot(account.name, securityService.authentication.principal, account.id)
        }
    }
}
