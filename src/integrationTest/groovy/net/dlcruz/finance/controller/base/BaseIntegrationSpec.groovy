package net.dlcruz.finance.controller.base

import net.dlcruz.finance.dao.repository.*
import net.dlcruz.finance.fixture.TestDataService
import net.dlcruz.finance.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.TestingAuthenticationToken
import spock.lang.Specification

class BaseIntegrationSpec extends Specification {

    static final String TEST_USER = 'test_user'

    @Autowired
    SecurityService securityService

    @Delegate
    @Autowired
    TestDataService testDataService

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
        securityService.getAuthentication() >> new TestingAuthenticationToken(authenticatedUser, null)
    }

    protected String getAuthenticatedUser() {
        TEST_USER
    }

    protected void cleanupAccounts() {
        allocationRepository.deleteAll()
        transactionRepository.deleteAll()
        goalRepository.deleteAll()
        budgetRepository.deleteAll()
        accountRepository.deleteAll()
    }
}
