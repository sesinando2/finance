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

    @Query('select distinct(a.name) from Allocation a join a.transaction t where t.account = :account')
    List<String> findAllNamesByAccount(@Param('account') Account account)

    @Query('select a from Allocation a join a.transaction t where t.account = :account and t.date >= :from and t.date <= :to')
    List<Allocation> findAllAccountAllocationBetween(@Param('account') Account account, @Param('from') Date from, @Param('to') Date to)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account')
    BigDecimal getAccountBalance(@Param('account') Account account)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name')
    BigDecimal getAllocationBalance(@Param('account') Account account, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.amount < 0 and t.date <= :date')
    BigDecimal getOverallDebitUpTo(@Param('account') Account account, @Param('date') date)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name and a.amount < 0 and t.date <= :date')
    BigDecimal getOverallDebitUpTo(@Param('account') Account account, @Param('date') date, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.amount > 0 and t.date <= :date')
    BigDecimal getOverallCredit(@Param('account') Account account, @Param('date') date)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name and a.amount > 0 and t.date <= :date')
    BigDecimal getOverallCredit(@Param('account') Account account, @Param('date') date, @Param('name') String name)

    @Query('select min(t.date) from Allocation a join a.transaction t where t.account = :account and a.name = :name and t.date <= :date')
    Timestamp getFirstTransactionDateBefore(@Param('account') Account account, @Param('date') Date date, @Param('name') String name)

    @Query('select max(t.date) from Allocation a join a.transaction t where t.account = :account and a.name = :name and t.date <= :date')
    Timestamp getLastTransactionDateUpTo(@Param('account') Account account, @Param('date') Date date, @Param('name') String name)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t = :transaction')
    BigDecimal getTransactionTotal(@Param('transaction') Transaction transaction)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and t.date <= :date')
    BigDecimal getAccountBalanceUpTo(@Param('account') Account account, @Param('date') Date date)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and t.date <= :date and a.name = :name')
    BigDecimal getAccountAllocationBalanceUpTo(@Param('account') Account account, @Param('date') Date date, @Param('name') String name)

    @Query('select sum(a.amount * -1) from Allocation a join a.transaction t where t.account = :account and a.name = :name and t.date >= :from and t.date <= :to and a.amount < 0')
    BigDecimal getAccountAllocationTotalDebit(@Param('account') Account account, @Param('name') String name, @Param('from') Date date, @Param('to') Date to)

    @Query('select sum(a.amount) from Allocation a join a.transaction t where t.account = :account and a.name = :name and t.date >= :from and t.date <= :to and a.amount >= 0')
    BigDecimal getAccountAllocationTotalCredit(@Param('account') Account account, @Param('name') String name, @Param('from') Date date, @Param('to') Date to)
}
