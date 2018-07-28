package net.dlcruz.finance.dao.domain

class Breakdown {

    Account account
    Budget budget

    String label

    BigDecimal balance
    BigDecimal totalCredit
    BigDecimal totalDebit
    BigDecimal allocatedAmount
}
