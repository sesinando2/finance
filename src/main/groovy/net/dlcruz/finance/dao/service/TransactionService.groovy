package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.base.EntityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TransactionService extends EntityService<Transaction, Long> {

    List<Transaction> findAllByAccount(Account account)

    Page<Transaction> findAllByAccount(Account account, Pageable pageable)
}