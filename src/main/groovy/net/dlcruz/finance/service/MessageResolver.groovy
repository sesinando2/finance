package net.dlcruz.finance.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class MessageResolver {

    @Autowired
    private MessageSource messageSource

    String getMessage(String code, List<Object> args = []) {
        messageSource.getMessage(code, args as Object[], LocaleContextHolder.locale)
    }

    String getMessage(String code, String defaultMessage, List<Object> args = []) {
        messageSource.getMessage(code, args as Object[], defaultMessage, LocaleContextHolder.locale)
    }

    String getMessage(List<String> codes, List<Object> args = []) {
        def result = tryAndGetMessage(codes, args)
        result ?: throwNoSuchMessageException(codes)
    }

    private String tryAndGetMessage(List<String> codes, List<Object> args) {
        for (def code : codes) {
            try {
                return getMessage(code, args)
            } catch (NoSuchMessageException) {
                continue
            }
        }
    }

    private void throwNoSuchMessageException(List<String> codes) {
        throw new NoSuchMessageException(codes.join(', ').trim(), LocaleContextHolder.locale)
    }
}
