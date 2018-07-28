package net.dlcruz.finance.api.controller

import net.dlcruz.finance.api.controller.base.BaseEntityController
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.service.BudgetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = '/budget', produces = 'application/json')
class BudgetController extends BaseEntityController<Budget, Long> {

    @Autowired
    BudgetService service

    @Override
    Budget create(@RequestBody Budget entity) {
        throw new HttpRequestMethodNotSupportedException('post')
    }
}
