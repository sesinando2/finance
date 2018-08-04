package net.dlcruz.finance.service

import net.dlcruz.finance.api.exception.EntityNotFoundException
import net.dlcruz.finance.dao.domain.Frequency
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


