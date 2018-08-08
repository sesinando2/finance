package net.dlcruz.finance.dao.service.implementation

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.repository.AllocationRepository
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.base.BaseEntityService
import net.dlcruz.finance.service.MessageResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
class AllocationEntityService extends BaseEntityService<Allocation, Long> implements AllocationService {

    @Autowired
    AllocationRepository repository

    @Autowired
    MessageResolver messageResolver

    @Autowired
    EntityValidationService validationService

    @Override
    ObjectValidationException validate(Allocation allocation) {
        def validationException = doJpaValidation(allocation)
        validateUniqueAllocationForTransaction(allocation, validationException)

        if (validationException.hasError) {
            throw validationException
        }

        validationException
    }

    @Override
    List<Allocation> findAllByAccount(Account account) {
        repository.findAllByAccount(account)
    }

    @Override
    List<String> findAllNamesByAccount(Account account) {
        repository.findAllNamesByAccount(account)
    }

    @Override
    List<Allocation> findAllByAccountBetween(Account account, Date from, Date to) {
        repository.findAllAccountAllocationBetween(account, from, to)
    }

    @Override
    List<Allocation> findAllByTransaction(Transaction transaction) {
        repository.findAllByTransaction(transaction)
    }

    @Override
    BigDecimal getOverallDebitUpTo(Account account, Date date) {
        repository.getOverallDebitUpTo(account, date) ?: 0
    }

    @Override
    BigDecimal getOverallDebitUpTo(Account account, Date date, String name) {
        repository.getOverallDebitUpTo(account, date, name) ?: 0
    }

    @Override
    BigDecimal getOverallCreditUpTo(Account account, Date date) {
        repository.getOverallCredit(account, date) ?: 0
    }

    @Override
    BigDecimal getOverallCreditUpTo(Account account, Date date, String name) {
        repository.getOverallCredit(account, date, name) ?: 0
    }

    @Override
    Date getFirstTransactionDateBefore(Account account, Date date, String name) {
        repository.getFirstTransactionDateBefore(account, date, name)?.toCalendar()?.time ?: date
    }

    @Override
    Date getlastTransactionDateUpTo(Account account, Date date, String name) {
        repository.getLastTransactionDateUpTo(account, date, name)?.toCalendar()?.time ?: date
    }

    @Override
    BigDecimal sum(List<Allocation> allocations) {
        allocations.sum { Allocation allocation -> allocation.amount } ?: 0
    }

    @Override
    BigDecimal getAccountBalanceUpTo(Account account, Date date) {
        repository.getAccountBalanceUpTo(account, date) ?: 0
    }

    @Override
    BigDecimal getAccountAllocationBalanceUpTo(Account account, Date date, String name) {
        repository.getAccountAllocationBalanceUpTo(account, date, name) ?: 0
    }

    @Override
    BigDecimal getAccountAllocationOverAllDebitBetween(Account account, String name, Date from, Date to) {
        repository.getAccountAllocationTotalDebit(account, name, from, to) ?: 0
    }

    @Override
    BigDecimal getAccountAllocationOverAllCreditBetween(Account account, String name, Date from, Date to) {
        repository.getAccountAllocationTotalCredit(account, name, from, to) ?: 0
    }

    private void validateUniqueAllocationForTransaction(Allocation allocation, ObjectValidationException validationException) {
        def existingAllocation = getExistingAllocation(allocation)

        if (existingAllocation) {
            def fieldName = 'name'
            def defaultMessage = messageResolver.getMessage(ObjectValidationException.UNIQUE_CONSTRAINT, [fieldName])
            validationException.pushUniqueConstraint(allocation, fieldName, existingAllocation, defaultMessage)
        }
    }

    private Allocation getExistingAllocation(Allocation allocation) {
        if (allocation.id == null) {
            repository.findByNameAndTransaction(allocation.name, allocation.transaction)
        } else {
            repository.findByNameAndTransactionAndIdNot(allocation.name, allocation.transaction, allocation.id)
        }
    }

    private ObjectValidationException doJpaValidation(Allocation allocation) {
        try {
            return super.validate(allocation)
        } catch (ObjectValidationException ex) {
            return ex
        }
    }

    @Override
    protected JpaRepository<Allocation, Long> getRepository() {
        repository
    }

    @Override
    protected EntityValidationService getValidationService() {
        validationService
    }
}
