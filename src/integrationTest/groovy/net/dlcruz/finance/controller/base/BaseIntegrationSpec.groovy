package net.dlcruz.finance.controller.base

import net.dlcruz.finance.dao.repository.AccountRepository
import net.dlcruz.finance.dao.repository.AllocationRepository
import net.dlcruz.finance.dao.repository.BudgetRepository
import net.dlcruz.finance.dao.repository.GoalRepository
import net.dlcruz.finance.dao.repository.TransactionRepository
import net.dlcruz.finance.fixture.IntegrationTestDataService
import net.dlcruz.finance.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.TestingAuthenticationToken
import spock.lang.Specification

class BaseIntegrationSpec extends Specification {

    static final String TEST_USER = 'test_user'

    @Autowired
    SecurityService securityService

    @Autowired
    IntegrationTestDataService testDataService

    @Autowired
    AccountRepository accountRepository

    @Autowired
    AllocationRepository allocationRepository

    @Autowired
    BudgetRepository budgetRepository

    @Autowired
    GoalRepository goalRepository

    @Autowired
    TransactionRepository transactionRepository

    void setup() {
        securityService.getAuthentication() >> new TestingAuthenticationToken(TEST_USER, null)
    }

    protected void cleanupAccounts() {
        allocationRepository.deleteAll()
        transactionRepository.deleteAll()
        goalRepository.deleteAll()
        budgetRepository.deleteAll()
        accountRepository.deleteAll()
    }
}
