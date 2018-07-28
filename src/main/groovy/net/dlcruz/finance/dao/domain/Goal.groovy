package net.dlcruz.finance.dao.domain

import javax.persistence.Entity
import javax.persistence.Transient
import javax.validation.constraints.NotNull

@Entity
class Goal extends Budget {

    Goal() {
        amount = 0
    }

    @NotNull
    BigDecimal targetAmount

    @NotNull
    Date targetDate

    @Transient
    boolean isCompleted() {
        balance >= targetAmount
    }

    @Transient
    boolean isExpired() {
        targetDate.clearTime() < new Date().clearTime()
    }

    @Transient
    BigDecimal getRemainingBalance() {
        targetAmount - balance
    }
}
