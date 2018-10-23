package net.dlcruz.finance.api.model

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget

class Breakdown {

    Account account
    Budget budget

    String label

    BigDecimal balance

    BigDecimal totalDebit
    BigDecimal totalCredit
    BigDecimal allocatedAmount

    BigDecimal incomeRate
    BigDecimal expenseRate
}
