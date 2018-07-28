package net.dlcruz.finance.api.controller

import net.dlcruz.finance.api.controller.base.BaseEntityController
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.service.GoalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = '/goal', produces = 'application/json')
class GoalController extends BaseEntityController<Goal, Long> {

    @Autowired
    GoalService service

    @Override
    Goal create(@RequestBody Goal entity) {
        throw new HttpRequestMethodNotSupportedException('post')
    }

    @Override
    Goal update(@PathVariable('id') Long id, @RequestBody Map properties) {
        properties.get('targetDate')?.with {
            properties.targetDate = new Date(it)
        }
        return super.update(id, properties)
    }

    @PostMapping('/calculate')
    Goal post(@RequestBody Goal entity) {
        entity?.with(service.&calculate >> service.&round)
    }
}
