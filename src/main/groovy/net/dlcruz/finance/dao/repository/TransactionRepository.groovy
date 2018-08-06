package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.sql.Timestamp

@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountOrderByDateDescIdDesc(Account account)

    Page<Transaction> findAllByAccountOrderByDateDescIdDesc(Account account, Pageable pageable)

    @Query('select min(t.date) from Transaction t where t.account = :account and t.date <= :date')
    Timestamp getFirstTransactionDateBefore(@Param('account') Account account, @Param('date') Date date)

    @Query('select max(t.date) from Allocation a join a.transaction t where t.account = :account and t.date <= :date')
    Timestamp getLastTransactionDateUpTo(@Param('account') Account account, @Param('date') Date date)
}