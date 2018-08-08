package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.base.EntityService

interface AllocationService extends EntityService<Allocation, Long> {

    List<Allocation> findAllByAccount(Account account)

    List<String> findAllNamesByAccount(Account account)

    List<Allocation> findAllByAccountBetween(Account account, Date from, Date to)

    BigDecimal getOverallDebitUpTo(Account account, Date date)

    BigDecimal getOverallDebitUpTo(Account account, Date date, String name)

    BigDecimal getOverallCreditUpTo(Account account, Date date)

    BigDecimal getOverallCreditUpTo(Account account, Date date, String name)

    Date getFirstTransactionDateBefore(Account account, Date date, String name)

    Date getlastTransactionDateUpTo(Account account, Date date, String name)

    List<Allocation> findAllByTransaction(Transaction transaction)

    BigDecimal sum(List<Allocation> allocations)

    BigDecimal getAccountBalanceUpTo(Account account, Date date)

    BigDecimal getAccountAllocationBalanceUpTo(Account account, Date date, String name)

    BigDecimal getAccountAllocationOverAllDebitBetween(Account account, String name, Date from, Date to)

    BigDecimal getAccountAllocationOverAllCreditBetween(Account account, String name, Date from, Date to)
}