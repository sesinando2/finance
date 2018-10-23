package net.dlcruz.finance.api.controller

import net.dlcruz.finance.api.controller.base.BaseEntityController
import net.dlcruz.finance.api.model.Breakdown
import net.dlcruz.finance.dao.domain.*
import net.dlcruz.finance.dao.service.*
import net.dlcruz.finance.dao.service.base.EntityService
import net.dlcruz.finance.service.FrequencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = '/account', produces = 'application/json')
class AccountController extends BaseEntityController<Account, Long> {

    @Autowired
    private AccountService service

    @Autowired
    private BudgetService budgetService

    @Autowired
    private BreakdownService breakdownService

    @Autowired
    private TransactionService transactionService

    @Autowired
    private FrequencyService frequencyService

    @Autowired
    private GoalService goalService

    @GetMapping('/{id}/total-{frequency}-breakdown')
    Breakdown getTotalBreakdown(@PathVariable Long id, @PathVariable('frequency') String frequencyString) {
        def account = getEntityAndThrowIfNotFound(id)
        def frequency = frequencyService.getFrequencyFrom(frequencyString)
        breakdownService.getTotalBreakdown(frequency, account)
    }

    @GetMapping('/{id}/{frequency}-breakdown')
    List<Breakdown> getBreakdown(@PathVariable Long id, @PathVariable('frequency') String frequencyString) {
        def account = getEntityAndThrowIfNotFound(id)
        def frequency = frequencyService.getFrequencyFrom(frequencyString)
        breakdownService.getBreakdown(frequency, account)
    }

    @PostMapping('/exist')
    Map<String, Boolean> exist(@RequestBody Account account) {
        [exist: service.exist(account)]
    }

    @Override
    @PutMapping('/{id}')
    Account update(@PathVariable('id') Long id, @RequestBody Map properties) {
        properties.remove('owner')
        super.update(id, properties)
    }

    @GetMapping('/{id}/budget')
    List<Budget> getBudgetList(@PathVariable('id') Long id) {
        def account = getEntityAndThrowIfNotFound(id)
        budgetService.findAllByAccount(account)
    }

    @GetMapping('/{id}/{frequency}-budget')
    List<Budget> convertBudget(@PathVariable('id') Long id, @PathVariable('frequency') String frequencyString) {
        def account = getEntityAndThrowIfNotFound(id)
        def targetFrequency = frequencyService.getFrequencyFrom(frequencyString)
        budgetService.findAllByAccount(account)
                .collect(budgetService.&convert.rcurry(targetFrequency) >> budgetService.&round)
    }

    @PostMapping('/{id}/budget')
    Budget createBudget(@PathVariable('id') Long id, @RequestBody Budget budget) {
        def account = getEntityAndThrowIfNotFound(id)
        budget.account = account
        budgetService.validate(budget)
        budgetService.save(budget)
    }

    @PostMapping('/{id}/budget/exist')
    Map<String, Boolean> budgetExist(@PathVariable('id') Long id, @RequestBody Budget budget) {
        def account = getEntityAndThrowIfNotFound(id)
        budget.account = account
        [exist: budgetService.exist(budget)]
    }

    @GetMapping('/{id}/goal')
    List<Goal> getGoalList(@PathVariable('id') Long id) {
        def account = getEntityAndThrowIfNotFound(id)
        goalService.findAllByAccount(account)
    }

    @GetMapping('/{id}/{frequency}-goal')
    List<Goal> convertGoal(@PathVariable('id') Long id, @PathVariable('frequency') String frequencyString) {
        def account = getEntityAndThrowIfNotFound(id)
        def targetFrequency = frequencyService.getFrequencyFrom(frequencyString)
        goalService.findAllByAccount(account)
                .collect(goalService.&convert.rcurry(targetFrequency) >> goalService.&round)
    }

    @PostMapping('/{id}/goal')
    Goal createGoal(@PathVariable('id') Long id, @RequestBody Goal goal) {
        def account = getEntityAndThrowIfNotFound(id)
        goal.account = account
        goalService.validate(goal)
        goalService.save(goal)
    }

    @PostMapping('/{id}/goal/exist')
    Map<String, Boolean> goalExist(@PathVariable('id') Long id, @RequestBody Goal goal) {
        def account = getEntityAndThrowIfNotFound(id)
        goal.account = account
        [exist: goalService.exist(goal)]
    }

    @GetMapping('/{id}/transaction')
    List<Transaction> getTransactionList(@PathVariable('id') Long id) {
        def account = getEntityAndThrowIfNotFound(id)
        transactionService.findAllByAccount(account)
    }

    @GetMapping('/{id}/transaction/page')
    Page<Transaction> getTransactionListByPage(@PathVariable('id') Long id, Pageable pageable) {
        def account = getEntityAndThrowIfNotFound(id)
        transactionService.findAllByAccount(account, pageable)
    }

    @PostMapping('/{id}/transaction')
    Transaction createTransaction(@PathVariable('id') Long id, @RequestBody Transaction transaction) {
        def account = getEntityAndThrowIfNotFound(id)
        transaction.account = account
        transaction.allocations.each { it.transaction = transaction }
        transactionService.save(transaction)
    }

    protected EntityService<Account, Long> getService() {
        service
    }
}
