package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.base.EntityService

interface AllocationService extends EntityService<Allocation, Long> {

    BigDecimal getBudgetBalance(Budget budget)

    List<Allocation> findAllByAccount(Account account)

    List<Allocation> findAllByAccountBetween(Account account, Date from, Date to)

    BigDecimal getAccountBalance(Account account)

    BigDecimal getAllocationBalance(Account account, String name)

    BigDecimal getOverallDebitUpTo(Account account, Date date)

    BigDecimal getOverallDebitUpTo(Account account, Date date, String name)

    BigDecimal getOverallCreditUpTo(Account account, Date date)

    BigDecimal getOverallCreditUpTo(Account account, Date date, String name)

    Date getFirstTransactionDateBefore(Account account, Date date, String name)

    Date getlastTransactionDateUpTo(Account account, Date date, String name)

    List<Allocation> findAllByTransaction(Transaction transaction)

    BigDecimal getTransactionTotal(Transaction transaction)

    BigDecimal sum(List<Allocation> allocations)

    BigDecimal getBalanceUpTo(Date date)
}