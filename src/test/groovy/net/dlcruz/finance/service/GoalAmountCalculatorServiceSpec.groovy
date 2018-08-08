package net.dlcruz.finance.service

import org.joda.time.DateTime
import org.joda.time.Period
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static net.dlcruz.finance.dao.domain.Frequency.*

@Unroll
class GoalAmountCalculatorServiceSpec extends Specification {

    @Subject
    GoalAmountCalculatorService service

    void setup() {
        service = new GoalAmountCalculatorService(frequencyService: new FrequencyService())
    }

    void 'should be able to correctly calculate how much to allocate #frequency with period of #period and target of #targetAmount'() {
        given:
        def today = new DateTime(new Date().clearTime())
        def targetDate = today + Period.parse(period)

        expect:
        service.calculateAmount(targetDate.toDate(), targetAmount, frequency) == expected

        where:
        frequency   | period    | targetAmount   | expected
        DAILY       | 'P10D'    | 1000           | 90.9090909091
        WEEKLY      | 'P5W'     | 500            | 83.3333333333
        MONTHLY     | 'P8M'     | 8000           | 888.8888888889
        MONTHLY     | 'P6M'     | 60             | 8.5714285714
        ANNUALLY    | 'P5Y'     | 50000          | 8333.3333333333
    }
}