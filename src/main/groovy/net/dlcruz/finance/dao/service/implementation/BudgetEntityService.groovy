package net.dlcruz.finance.dao.service.implementation

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.repository.BudgetRepository
import net.dlcruz.finance.dao.service.AllocationService
import net.dlcruz.finance.dao.service.BudgetService
import net.dlcruz.finance.dao.service.GoalService
import net.dlcruz.finance.dao.service.base.BaseBudgetEntityService
import net.dlcruz.finance.service.FrequencyService
import net.dlcruz.finance.service.MessageResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BudgetEntityService extends BaseBudgetEntityService<Budget> implements BudgetService {

    @Autowired
    BudgetRepository repository

    @Autowired
    EntityValidationService validationService

    @Autowired
    MessageResolver messageResolver

    @Autowired
    AllocationService allocationService

    @Autowired
    FrequencyService frequencyService

    @Autowired
    GoalService goalService

    @Override
    List<Budget> findAllByAccount(Account account) {
        super.findAllByAccount(account).collect {
            setAdditionalProperties(it)
        }
    }

    @Override
    Budget setAdditionalProperties(Budget budget) {
        budget
    }

    Goal setAdditionalProperties(Goal goal) {
        goalService.setAdditionalProperties(goal)
    }
}