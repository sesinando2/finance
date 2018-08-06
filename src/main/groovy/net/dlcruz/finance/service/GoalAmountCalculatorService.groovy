package net.dlcruz.finance.service

import net.dlcruz.finance.dao.domain.Frequency
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoalAmountCalculatorService {

    @Autowired
    FrequencyService frequencyService

    BigDecimal calculateAmount(Date targetDate, BigDecimal remainingAmount, Frequency frequency) {
        def now = frequencyService.getRoundedDownStartDate(new Date(), frequency)

        if (now >= targetDate) {
            return remainingAmount
        }

        def roundedTargetDate = frequencyService.getRoundedUpEndDate(targetDate, frequency)
        def result = frequencyService.getDuration(now, roundedTargetDate, frequency)
        result > 0 ? remainingAmount / result : remainingAmount
    }
}
