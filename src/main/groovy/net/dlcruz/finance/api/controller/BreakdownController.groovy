package net.dlcruz.finance.api.controller


import net.dlcruz.finance.dao.domain.Breakdown
import net.dlcruz.finance.dao.service.BreakdownService
import net.dlcruz.finance.service.FrequencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = 'application/json')
class BreakdownController {

    @Autowired
    private FrequencyService frequencyService

    @Autowired
    private BreakdownService breakdownService

    @GetMapping('/{frequency}-breakdown')
    List<Breakdown> get(@PathVariable('frequency') String frequencyString) {
        def frequency = frequencyService.getFrequencyFrom(frequencyString)
        breakdownService.getBreakdown(frequency)
    }
}
