package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.base.EntityService

interface AllocationService extends EntityService<Allocation, Long> {

    BigDecimal getBudgetBalance(Budget budget)

    List<Allocation> findAllByAccount(Account account)

    List<Allocation> findAllByAccountStartingFrom(Account account, Date date)

    BigDecimal getAccountBalance(Account account)

    BigDecimal getAllocationBalance(Account account, String name)

    BigDecimal getOverallDebit(Account account)

    BigDecimal getOverallDebit(Account account, String name)

    BigDecimal getOverallCredit(Account account)

    BigDecimal getOverallCredit(Account account, String name)

    Date getFirstTransactionDate(Account account, String name)

    Date getlastTransactionDate(Account account, String name)

    List<Allocation> findAllByTransaction(Transaction transaction)

    BigDecimal getTransactionTotal(Transaction transaction)

    BigDecimal sum(List<Allocation> allocations)
}