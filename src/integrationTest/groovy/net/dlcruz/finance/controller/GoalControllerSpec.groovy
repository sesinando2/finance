package net.dlcruz.finance.controller

import groovy.time.TimeCategory
import net.dlcruz.finance.config.IntegrationTestConfiguration
import net.dlcruz.finance.controller.base.BaseControllerSpec
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.service.GoalService
import org.joda.time.DateTime
import org.joda.time.Period
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Stepwise

import static net.dlcruz.finance.dao.domain.Frequency.*
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@Stepwise
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class GoalControllerSpec extends BaseControllerSpec {

    @Autowired
    GoalService goalService

    @Shared Goal goal

    void 'should not allow creation of goal from the goal endpoint'() {
        given:
        def targetDate = TimeCategory.getMonths(5).from.now
        def goal = new Goal(name: 'Test Goal', frequency: MONTHLY, targetDate: targetDate, targetAmount: 5000)

        when:
        def response = restTemplate.postForEntity('/goal', goal, Map)

        then:
        response.statusCode == HttpStatus.METHOD_NOT_ALLOWED
    }

    void 'should be able get the goal through the goal endpoint'() {
        given:
        goal = testDataService.newGoalBuilder().entity

        when:
        def response = restTemplate.getForEntity('/goal/{id}', Goal, goal.id)

        then:
        response.statusCode == HttpStatus.OK
        response.body.name == goal.name
    }

    void 'should be able to update the goal and get the correct calculation'() {
        given:
        def today = new DateTime(new Date().clearTime())
        def targetDate = (today + Period.parse(period)).toDate()

        and:
        def data = [frequency: frequency, targetAmount: targetAmount, targetDate: targetDate.time]

        when:
        restTemplate.put('/goal/{id}', data, goal.id)

        and:
        goal = goalService.get(goal.id)

        then:
        goal.frequency == frequency
        goal.targetAmount == targetAmount
        goal.targetDate == targetDate
        goal.amount == expected

        where:
        frequency   | period    | targetAmount   | expected
        DAILY       | 'P10D'    | 1000           | 100
        WEEKLY      | 'P5W'     | 500            | 100
        MONTHLY     | 'P8M'     | 8000           | 1000
        MONTHLY     | 'P6M'     | 60             | 10
        ANNUALLY    | 'P5Y'     | 50000          | 10000
    }

    void 'should be able to delete goal'() {
        when:
        restTemplate.delete('/goal/{id}', goal.id)

        then:
        goalService.get(goal.id) == null

        when:
        def response = restTemplate.getForEntity('/goal/{id}', Map, goal.id)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        cleanup:
        cleanupAccounts()
    }
}
