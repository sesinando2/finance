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
        Calendar calendar = Calendar.instance

        switch (frequency) {
            case Frequency.DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, -1 * ago)
                return calendar.time

            case Frequency.WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, -1 * ago)
                break

            case Frequency.FORTNIGHTLY:
                calendar.add(Calendar.WEEK_OF_YEAR, -2 * ago)
                break

            case Frequency.MONTHLY:
                calendar.add(Calendar.MONTH, -1 * ago)
                break

            case Frequency.ANNUALLY:
                calendar.add(Calendar.YEAR, -1 * ago)
                break
        }

        calendar.time
    }

    Date getEndDateForBreakdown(Frequency frequency, int ago = 1) {
        Calendar calendar = Calendar.instance

        switch (frequency) {
            case Frequency.DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, (-1 * ago) + 1)
                break

            case Frequency.WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, (-1 * ago) + 1)
                break

            case Frequency.FORTNIGHTLY:
                calendar.add(Calendar.WEEK_OF_YEAR, (-2 * ago) + 1)
                break

            case Frequency.MONTHLY:
                calendar.add(Calendar.MONTH, (-1 * ago) + 1)
                break

            case Frequency.ANNUALLY:
                calendar.add(Calendar.YEAR, (-1 * ago) + 1)
                break
        }

        calendar.time
    }

    private Interval getInterval(Date first, Date last) {
        def from = first
        def to = last
        new Interval(from.time, to.time)
    }

    Date getRoundedDownStartDate(Date date, Frequency frequency) {
        def calendar = date.clearTime().toCalendar()
        getCalendarField(frequency)?.with {
            calendar.set(it, calendar.getActualMinimum(it))
        }
        calendar.time
    }

    Date getRoundedUpEndDate(Date date, Frequency frequency) {
        def calendar = date.clearTime().toCalendar()
        getCalendarField(frequency)?.with {
            calendar.set(it, calendar.getActualMaximum(it))
        }
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.time
    }

    private Integer getCalendarField(Frequency frequency) {
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
}


