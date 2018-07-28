package net.dlcruz.finance.controller.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

class BaseControllerSpec extends BaseIntegrationSpec {

    @Autowired
    TestRestTemplate restTemplate
}
