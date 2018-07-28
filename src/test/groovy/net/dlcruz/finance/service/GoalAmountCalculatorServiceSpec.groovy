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
        service = new GoalAmountCalculatorService()
    }

    void 'should be able to correctly calculate how much to allocate #frequency with period of #period and target of #targetAmount'() {
        given:
        def today = new DateTime(new Date().clearTime())
        def targetDate = today + Period.parse(period)

        expect:
        service.calculateAmount(targetDate.toDate(), targetAmount, frequency) == expected

        where:
        frequency   | period    | targetAmount   | expected
        DAILY       | 'P10D'    | 1000           | 100
        WEEKLY      | 'P5W'     | 500            | 100
        MONTHLY     | 'P8M'     | 8000           | 1000
        MONTHLY     | 'P6M'     | 60             | 10
        ANNUALLY    | 'P5Y'     | 50000          | 10000
    }
}