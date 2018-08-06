package net.dlcruz.finance.dao.service.implementation

import groovy.time.TimeCategory
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.repository.TransactionRepository
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.TransactionService
import net.dlcruz.finance.dao.service.base.BaseEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
class TransactionEntityService extends BaseEntityService<Transaction, Long> implements TransactionService {

    @Autowired
    EntityValidationService validationService

    @Autowired
    TransactionRepository repository

    @Autowired
    AllocationService allocationService

    @Override
    @Transactional
    List<Transaction> findAllByAccount(Account account) {
        repository.findAllByAccountOrderByDateDescIdDesc(account)
    }

    @Override
    Page<Transaction> findAllByAccount(Account account, Pageable pageable) {
        repository.findAllByAccountOrderByDateDescIdDesc(account, pageable)
    }

    @Override
    Date getFirstTransactionDateBefore(Account account, Date date) {
        repository.getFirstTransactionDateBefore(account, date)?.toCalendar()?.time ?: date
    }

    @Override
    Date getLastTransactionDateUpTo(Account account, Date date) {
        repository.getLastTransactionDateUpTo(account, date)?.toCalendar()?.time ?: date
    }
}
