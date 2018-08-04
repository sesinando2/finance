package net.dlcruz.finance.service

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static net.dlcruz.finance.dao.domain.Frequency.*

@Unroll
class FrequencyServiceSpec extends Specification {

    @Subject FrequencyService service

    void setup() {
        service = new FrequencyService()
    }

    void 'test frequency conversion from #from to #to'() {
        when:
        def converted = service.getConverter(from, to)

        then:
        converted.call(value) == expected

        where:
        from        | to            | value     | expected
        WEEKLY      | DAILY         | 7         | 1
        FORTNIGHTLY | DAILY         | 14        | 1
        FORTNIGHTLY | WEEKLY        | 10        | 5
        DAILY       | FORTNIGHTLY   | 2         | 28
        WEEKLY      | FORTNIGHTLY   | 25        | 50
        DAILY       | WEEKLY        | 1         | 7
        DAILY       | DAILY         | 1         | 1
        MONTHLY     | DAILY         | 50        | 1.6438356164
        DAILY       | ANNUALLY      | 1         | 365
        MONTHLY     | ANNUALLY      | 1         | 12
        ANNUALLY    | WEEKLY        | 53        | 1.0164383565
        WEEKLY      | MONTHLY       | 470       | 2042.2619047632
        MONTHLY     | FORTNIGHTLY   | 300       | 138.0821917804
        MONTHLY     | WEEKLY        | 300       | 69.0410958902
    }
}
