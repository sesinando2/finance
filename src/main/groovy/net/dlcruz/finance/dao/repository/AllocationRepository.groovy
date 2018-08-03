package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.sql.Timestamp

@Repository
interface AllocationRepository extends JpaRepository<Allocation, Long> {

    List<Allocation> findAllByTransaction(Transaction transaction)

    Allocation findByNameAndTransaction(String name, Transaction transaction)

    Allocation findByNameAndTransactionAndIdNot(String name, Transaction transaction, Long id)

    @Query('select a from Allocation a join a.transaction t where t.account = :account')
    List<Allocation> findAllByAccount(@Param('account') Account account)

    @Query('select a from Allocation a join a.transaction t where t.account = :account and t.date >= :date')
    List<Allocation> findAllByAccountAndDateGreaterThanOrEqualTo(@Param('account') Account account, @Param('date') Date date)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account')
    BigDecimal getAccountBalance(@Param('account') Account account)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name')
    BigDecimal getAllocationBalance(@Param('account') Account account, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.amount < 0')
    BigDecimal getOverallDebit(@Param('account') Account account)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name and a.amount < 0')
    BigDecimal getOverallDebit(@Param('account') Account account, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.amount > 0')
    BigDecimal getOverallCredit(@Param('account') Account account)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name and a.amount > 0')
    BigDecimal getOverallCredit(@Param('account') Account account, @Param('name') String name)

    @Query('select min(t.date) from Allocation a join a.transaction t where t.account = :account and a.name = :name')
    Timestamp getFirstTransactionDate(@Param('account') Account account, @Param('name') String name)

    @Query('select max(t.date) from Allocation a join a.transaction t where t.account = :account and a.name = :name')
    Timestamp getLastTransactionDate(@Param('account') Account account, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t = :transaction')
    BigDecimal getTransactionTotal(@Param('transaction') Transaction transaction)
}
