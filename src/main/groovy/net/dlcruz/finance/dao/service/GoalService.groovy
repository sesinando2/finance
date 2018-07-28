package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.service.base.BaseBudgetService

interface GoalService extends BaseBudgetService<Goal> {

    Goal calculate(Goal goal)

    Goal setAdditionalProperties(Goal goal)
}