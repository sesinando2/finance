package net.dlcruz.finance.dao.service.implementation

import groovy.time.TimeCategory
import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.repository.AllocationRepository
import net.dlcruz.finance.dao.service.base.BaseEntityService
import net.dlcruz.finance.dao.service.AllocationService
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
    List<Allocation> findAllByAccountStartingFrom(Account account, Date date) {
        repository.findAllByAccountAndDateGreaterThanOrEqualTo(account, date)
    }

    @Override
    List<Allocation> findAllByTransaction(Transaction transaction) {
        repository.findAllByTransaction(transaction)
    }

    @Override
    BigDecimal getBudgetBalance(Budget budget) {
        repository.getAllocationBalance(budget.account, budget.name)
    }

    @Override
    BigDecimal getAccountBalance(Account account) {
        repository.getAccountBalance(account)
    }

    @Override
    BigDecimal getAllocationBalance(Account account, String name) {
        repository.getAllocationBalance(account, name)
    }

    @Override
    BigDecimal getOverallDebit(Account account) {
        repository.getOverallDebit(account) ?: 0
    }

    @Override
    BigDecimal getOverallDebit(Account account, String name) {
        repository.getOverallDebit(account, name) ?: 0
    }

    @Override
    BigDecimal getOverallCredit(Account account) {
        repository.getOverallCredit(account) ?: 0
    }

    @Override
    BigDecimal getOverallCredit(Account account, String name) {
        repository.getOverallCredit(account, name) ?: 0
    }

    @Override
    Date getFirstTransactionDate(Account account, String name) {
        repository.getFirstTransactionDate(account, name) ?: TimeCategory.getDay(1).ago
    }

    @Override
    Date getlastTransactionDate(Account account, String name) {
        repository.getLastTransactionDate(account, name) ?: new Date()
    }

    @Override
    BigDecimal getTransactionTotal(Transaction transaction) {
        repository.getTransactionTotal(transaction)
    }

    @Override
    BigDecimal sum(List<Allocation> allocations) {
        allocations.sum { Allocation allocation -> allocation.amount } ?: 0
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
