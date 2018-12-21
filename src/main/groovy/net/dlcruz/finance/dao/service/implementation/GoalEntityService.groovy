package net.dlcruz.finance.dao.service.implementation

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.repository.GoalRepository
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.GoalService
import net.dlcruz.finance.dao.service.base.BaseBudgetEntityService
import net.dlcruz.finance.service.FrequencyService
import net.dlcruz.finance.service.GoalAmountCalculatorService
import net.dlcruz.finance.service.MessageResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GoalEntityService extends BaseBudgetEntityService<Goal> implements GoalService {

    @Autowired
    GoalRepository repository

    @Autowired
    EntityValidationService validationService

    @Autowired
    AllocationService allocationService

    @Autowired
    GoalAmountCalculatorService amountCalculatorService

    @Autowired
    FrequencyService frequencyService

    @Autowired
    MessageResolver messageResolver

    @Override
    Goal get(Long id) {
        super.get(id)?.with(this.&setAdditionalProperties)
    }

    @Override
    List<Goal> findAllByAccount(Account account) {
        super.findAllByAccount(account).collect(this.&setAdditionalProperties)
    }

    @Override
    Goal setAdditionalProperties(Goal goal) {
        calculate(goal)
    }

    @Override
    List<Goal> list() {
        super.list().collect(this.&setAdditionalProperties)
    }

    @Override
    Page<Goal> listByPage(Pageable pageable) {
        super.listByPage(pageable).collect(this.&setAdditionalProperties)
    }

    Goal calculate(Goal goal) {
        if (goal.completed) {
            goal.amount = 0
        } else if (goal.targetDate && goal.remainingBalance && goal.frequency) {
            def from = getStartingDateToCalculateFrom(goal)
            goal.amount = amountCalculatorService.calculateAmount(from, goal.targetDate, goal.remainingBalance, goal.frequency)
        }

        goal
    }

    private Date getStartingDateToCalculateFrom(Goal goal) {
        def lastAllocatedCredit = allocationService.getLastAllocatedCredit(goal.account, goal.name)
        def nextExpectedCreditAllocation = lastAllocatedCredit ? frequencyService.getRelatedDateTo(lastAllocatedCredit, goal.frequency, 1).time : new Date()
        [nextExpectedCreditAllocation, new Date()].max()
    }
}
