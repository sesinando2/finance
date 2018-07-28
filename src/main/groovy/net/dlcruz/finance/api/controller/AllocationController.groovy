package net.dlcruz.finance.api.controller

import net.dlcruz.finance.api.controller.base.BaseEntityController
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.service.base.EntityService
import net.dlcruz.finance.dao.service.AllocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = '/allocation', produces = 'application/json')
class AllocationController extends BaseEntityController<Allocation, Long> {

    @Autowired
    AllocationService service
}
