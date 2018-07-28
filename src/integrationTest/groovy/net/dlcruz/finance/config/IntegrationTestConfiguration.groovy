package net.dlcruz.finance.config

import net.dlcruz.finance.service.SecurityService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import spock.mock.DetachedMockFactory

class IntegrationTestConfiguration extends ResourceServerConfigurerAdapter {

    private final detachedMockFactory = new DetachedMockFactory()

    @Bean
    SecurityService securityService() {
        detachedMockFactory.Mock(SecurityService)
    }

    @Override
    void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers('/**').permitAll()
    }
}