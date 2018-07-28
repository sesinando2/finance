package net.dlcruz.finance.service

import spock.lang.Specification
import spock.lang.Subject

import static net.dlcruz.finance.dao.domain.Frequency.*

class FrequencyServiceSpec extends Specification {

    @Subject FrequencyService service

    void setup() {
        service = new FrequencyService()
    }

    void 'test frequency conversion'() {
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
        MONTHLY     | DAILY         | 30.4167   | 1
        DAILY       | ANNUALLY      | 1         | 365
        MONTHLY     | ANNUALLY      | 1         | 12
        ANNUALLY    | WEEKLY        | 52.1429   | 1
        WEEKLY      | MONTHLY       | 470       | 2042.2628
    }
}
