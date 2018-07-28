package net.dlcruz.finance.service

import net.dlcruz.finance.dao.domain.Frequency
import org.joda.time.*
import org.springframework.stereotype.Service

import static net.dlcruz.finance.dao.domain.Frequency.*

@Service
class GoalAmountCalculatorService {

    private final Map<Frequency, Closure<BigDecimal>> calculators = [
            (DAILY): { Interval interval, BigDecimal remainingAmount ->
                def result = Days.daysIn(interval)
                result.days > 0 ? remainingAmount / result.days : remainingAmount
            },
            (WEEKLY): { Interval interval, BigDecimal remainingAmount ->
                def result = Weeks.weeksIn(interval)
                result.weeks > 0 ? remainingAmount / result.weeks : remainingAmount
            },
            (FORTNIGHTLY): { Interval interval, BigDecimal remainingAmount ->
                def result = getCalculator(WEEKLY).call(interval, remainingAmount)
                result / 2
            },
            (MONTHLY): { Interval interval, BigDecimal remainingAmount ->
                def result = Months.monthsIn(interval)
                result.months > 0 ? remainingAmount / result.months : remainingAmount
            },
            (ANNUALLY): { Interval interval, BigDecimal remainingAmount ->
                def result = Years.yearsIn(interval)
                result.years > 0 ? remainingAmount / result.years : remainingAmount
            }
    ].asImmutable()

    Closure<BigDecimal> getCalculator(Frequency frequency) {
        calculators.get(frequency)
    }

    BigDecimal calculateAmount(Date targetDate, BigDecimal remainingAmount, Frequency frequency) {
        def now = new Date().clearTime()

        if (now >= targetDate) {
            return remainingAmount
        }

        def interval = new Interval(new Date().clearTime().time, targetDate.time)
        getCalculator(frequency).call(interval, remainingAmount)
    }
}
