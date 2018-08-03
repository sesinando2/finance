package net.dlcruz.finance.dao.service.implementation

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
    Date getFirstTransactionDate(Account account) {
        repository.getFirstTransactionDate(account).toCalendar().time
    }

    @Override
    Date getLastTransactionDate(Account account) {
        repository.getLastTransactionDate(account).toCalendar().time
    }
}
