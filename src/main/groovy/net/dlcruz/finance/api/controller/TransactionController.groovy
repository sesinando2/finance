package net.dlcruz.finance.api.controller

import net.dlcruz.finance.api.controller.base.BaseEntityController
import net.dlcruz.finance.dao.domain.Allocation
import net.dlcruz.finance.dao.domain.Transaction
import net.dlcruz.finance.dao.service.base.EntityService
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = '/transaction', produces = 'application/json')
class TransactionController extends BaseEntityController<Transaction, Long> {

    @Autowired
    TransactionService service

    @Autowired
    AllocationService allocationService

    @GetMapping('/{id}/allocation')
    List<Allocation> getAllocationList(@PathVariable('id') Long id) {
        def transaction = getEntityAndThrowIfNotFound(id)
        allocationService.findAllByTransaction(transaction)
    }

    @PostMapping('/{id}/allocation')
    Allocation createAllocation(@PathVariable('id') Long id, @RequestBody Allocation allocation) {
        allocation.transaction = getEntityAndThrowIfNotFound(id)
        allocationService.save(allocation)
    }
}
