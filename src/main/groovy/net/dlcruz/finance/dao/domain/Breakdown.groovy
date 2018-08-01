package net.dlcruz.finance.dao.domain

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
