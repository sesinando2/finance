package net.dlcruz.finance.service

import net.dlcruz.finance.api.exception.EntityNotFoundException
import net.dlcruz.finance.dao.domain.Frequency
import org.joda.time.*
import org.springframework.stereotype.Service

import static net.dlcruz.finance.dao.domain.Frequency.*

@Service
class FrequencyService {

    private static final Map<Range<Frequency>, Closure<BigDecimal>> conversions = [
        (DAILY..WEEKLY)         : { BigDecimal value -> value * 7 },
        (DAILY..ANNUALLY)       : { BigDecimal value -> value * 365.25 },
        (WEEKLY..FORTNIGHTLY)   : { BigDecimal value -> value * 2 },
        (MONTHLY..ANNUALLY)     : { BigDecimal value -> value * 12 },
        (ANNUALLY..MONTHLY)     : { BigDecimal value -> value / 12 },
        (FORTNIGHTLY..WEEKLY)   : { BigDecimal value -> value / 2 },
        (ANNUALLY..DAILY)       : { BigDecimal value -> value / 365.25 },
        (WEEKLY..DAILY)         : { BigDecimal value -> value / 7 }
    ].asImmutable()

    Frequency getFrequencyFrom(String frequencyString) {
        def frequency = Frequency.getFromPath(frequencyString)

        if (!frequencyString) {
            throw new EntityNotFoundException(Frequency, frequencyString)
        }

        frequency
    }

    Closure<BigDecimal> getConverter(Frequency from, Frequency to) {
        def path = findConversionPath(from, to)
        composeConversionClosure(path)
    }

    int getDuration(Date first, Date last, Frequency frequency) {
        switch (frequency) {
            case Frequency.DAILY:
                return Days.daysIn(new Interval(first.time, last.time)).days

            case Frequency.WEEKLY:
                return Weeks.weeksIn(getInterval(first, last)).weeks

            case Frequency.FORTNIGHTLY:
                return Weeks.weeksIn(getInterval(first, last)).weeks / 2

            case Frequency.MONTHLY:
                return Months.monthsIn(getInterval(first, last)).months

            case Frequency.ANNUALLY:
                return Years.yearsIn(getInterval(first, last)).years
        }
    }

    Date getStartDateForBreakdown(Frequency frequency, int ago = 1) {
        getRelatedDateTo(new Date(), frequency, -ago).time
    }

    Date getEndDateForBreakdown(Frequency frequency, Date startDate) {
        def calendar = getRelatedDateTo(startDate, frequency, 1)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.time
    }

    Calendar getRelatedDateTo(Date date, Frequency frequency, int gap) {
        def calendar = date.toCalendar()
        calendar.add(getCorrespondingCalendarField(frequency), getModifierFor(frequency, gap))
        calendar
    }

    Date getRoundedDownStartDate(Date date, Frequency frequency) {
        def calendar = date.clearTime().toCalendar()
        setActualForRounding(Actual.MINIMUM, frequency, calendar)
        calendar.time
    }

    Date getRoundedUpEndDate(Date date, Frequency frequency) {
        def calendar = date.toCalendar()
        setActualForRounding(Actual.MAXIMUM, frequency, calendar)
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND))
        calendar.time
    }

    private Interval getInterval(Date first, Date last) {
        def from = first
        def to = last
        new Interval(from.time, to.time)
    }

    private void setActualForRounding(Actual actual, Frequency frequency, Calendar calendar) {
        if (frequency != DAILY) {
            def calendarField = getCalendarFieldToRound(frequency)
            calendar.set(calendarField, actual.value(calendar, calendarField))
        }
    }

    private Integer getCalendarFieldToRound(Frequency frequency) {
        switch (frequency) {
            case WEEKLY:
            case FORTNIGHTLY:
                return Calendar.DAY_OF_WEEK

            case MONTHLY:
                return Calendar.DAY_OF_MONTH

            case ANNUALLY:
                return Calendar.DAY_OF_YEAR

            default:
                return null
        }
    }

    private Integer getCorrespondingCalendarField(Frequency frequency) {
        switch (frequency) {
            case DAILY:
                return Calendar.DAY_OF_YEAR

            case WEEKLY:
            case FORTNIGHTLY:
                return Calendar.WEEK_OF_MONTH

            case MONTHLY:
                return Calendar.MONTH

            case ANNUALLY:
                return Calendar.YEAR
        }
    }

    private Integer getModifierFor(Frequency frequency, int modifier) {
        switch (frequency) {
            case FORTNIGHTLY:
                return modifier * 2
            default:
                return modifier
        }
    }

    private Closure<BigDecimal> composeConversionClosure(List<Frequency> path) {
        def closure = { BigDecimal value -> value }

        if (path.size() > 1) {
            path.inject { Frequency previous, Frequency next ->
                closure >>= conversions.get(previous..next)
                next
            }
        }

        closure
    }

    private List<Frequency> findConversionPath(Frequency from, Frequency to) {
        List<Frequency> path = []

        def frequenciesToCheck = Frequency.values().toList()
        def currentNode = from

        while (currentNode != to) {
            def nextNode = findNextNode(currentNode, frequenciesToCheck - path)
            def hasNextNode = nextNode != null
            def atStartingNode = currentNode == from

            if (hasNextNode) {
                path << currentNode
                currentNode = nextNode
            } else if (atStartingNode) {
                throw new RuntimeException("No conversion path found for ${from} -> ${to}")
            } else {
                frequenciesToCheck -= currentNode
                currentNode = path.pop()
            }

            if (currentNode == to) {
                path << currentNode
                break
            }
        }

        path
    }

    private Frequency findNextNode(Frequency start, List<Frequency> frequenciesToCheck) {
        for (end in frequenciesToCheck.reverse()) {

            def conversion = conversions.get(start..end)

            if (conversion) {
                return end
            }
        }
    }

    private enum Actual {

        MINIMUM({ Calendar calendar, int field -> calendar.getActualMinimum(field) }),
        MAXIMUM({ Calendar calendar, int field -> calendar.getActualMaximum(field) })

        private Closure<Integer> closure

        private Integer value(Calendar calendar, int field) {
            closure(calendar, field)
        }

        Actual(Closure closure) {
            this.closure = closure
        }
    }
}


