package net.dlcruz.finance.service

import net.dlcruz.finance.dao.domain.Frequency
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoalAmountCalculatorService {

    @Autowired
    FrequencyService frequencyService

    BigDecimal calculateAmount(Date from, Date targetDate, BigDecimal remainingAmount, Frequency frequency) {
        def calculateFrom = frequencyService.getRoundedDownStartDate(from, frequency)

        if (calculateFrom >= targetDate) {
            return remainingAmount
        }

        def roundedTargetDate = frequencyService.getRoundedUpEndDate(targetDate, frequency) + 1
        def result = frequencyService.getDuration(calculateFrom, roundedTargetDate, frequency)
        result > 0 ? remainingAmount / result : remainingAmount
    }
}
