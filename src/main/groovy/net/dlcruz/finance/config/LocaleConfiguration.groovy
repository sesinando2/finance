package net.dlcruz.finance.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor

@Configuration
class LocaleConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    LocaleResolver localeResolver() {
        new AcceptHeaderLocaleResolver(defaultLocale: Locale.ENGLISH)
    }

    @Bean
    LocaleChangeInterceptor localeChangeInterceptor() {
        new LocaleChangeInterceptor(paramName: 'lang')
    }

    @Override
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}
