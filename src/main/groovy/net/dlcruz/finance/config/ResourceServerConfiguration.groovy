package net.dlcruz.finance.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableResourceServer
class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value('${security.signing-key}')
    private String signingKey

    @Value('${security.auth.resource-id}')
    private String resourceId

    @Bean
    JwtAccessTokenConverter accessTokenConverter() {
        new JwtAccessTokenConverter(signingKey: signingKey)
    }

    @Bean
    JwtTokenStore tokenStore() {
        new JwtTokenStore(accessTokenConverter())
    }

    @Bean
    @Primary
    DefaultTokenServices tokenServices() {
        new DefaultTokenServices(tokenStore: tokenStore(), supportRefreshToken: true)
    }

    @Override
    void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceId)
            .tokenServices(tokenServices())
    }

    @Override
    void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .and()
            .authorizeRequests()
            .antMatchers('/actuators/**', '/api-docs/**').permitAll()
            .antMatchers('/**').authenticated()
    }

    @Bean
    FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        CorsConfiguration config = new CorsConfiguration()
        config.setAllowCredentials(true)
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source))
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE)
        bean
    }
}
