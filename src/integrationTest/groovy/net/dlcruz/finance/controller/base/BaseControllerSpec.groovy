package net.dlcruz.finance.controller.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class BaseControllerSpec extends BaseIntegrationSpec {

    @Autowired
    TestRestTemplate restTemplate

    void setup() {
        setUser(TEST_USER)
    }

    protected void setUser(String user) {
        restTemplate.restTemplate.interceptors = [this.&intercept.curry(user) as ClientHttpRequestInterceptor]
    }

    protected ClientHttpResponse intercept(String user, HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.headers.add('user', user)
        execution.execute(request, body)
    }
}
