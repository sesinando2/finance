package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.repository.base.BaseBudgetRepository
import org.springframework.stereotype.Repository

@Repository
interface BudgetRepository extends BaseBudgetRepository<Budget> {

}
