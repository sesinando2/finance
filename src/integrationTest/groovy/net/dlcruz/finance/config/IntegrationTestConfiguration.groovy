package net.dlcruz.finance.config

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import spock.mock.DetachedMockFactory

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class IntegrationTestConfiguration extends ResourceServerConfigurerAdapter {

    private final detachedMockFactory = new DetachedMockFactory()

    @Override
    void configure(HttpSecurity http) throws Exception {
        def filter = this.&doFilterInternal as OncePerRequestFilter

        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .addFilterBefore(filter, BasicAuthenticationFilter)
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        def username = request.getHeader('user')
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(username, null, [] as String[])
        filterChain.doFilter(request, response)
    }
}