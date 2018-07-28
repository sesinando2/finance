package net.dlcruz.finance.api.exception

import net.dlcruz.finance.api.model.ApiError
import net.dlcruz.finance.api.model.ApiErrorResponse
import net.dlcruz.finance.service.MessageResolver
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@Service
class ApiErrorTransformerService {

    static final String TYPE_MISMATCH = 'typeMismatch'
    static final String METHOD_ARGUMENT_TYPE_MISMATCH = 'methodArgumentTypeMismatch'
    static final String NO_HANDLER_FOUND = 'noHandlerFound'
    static final String MISSING_SERVLET_REQUEST_PART = 'missingServletRequestPart'
    static final String MISSING_SERVLET_REQUEST_PARAMETER = 'missingServletRequestParameter'
    static final String HTTP_REQUEST_METHOD_NOT_SUPPORTED = 'httpRequestMethodNotSupported'
    static final String HTTP_MEDIA_TYPE_NOT_SUPPORTED = 'httpMediaTypeNotSupported'
    static final String ENTITY_NOT_FOUND = 'entityNotFound'

    @Autowired
    private MessageResolver messageResolverService

    ApiErrorResponse getErrorResponseFor(Exception exception) {
        errorResponseFrom(exception).with(this.&convertGstringsToString)
    }

    // 400
    private ApiErrorResponse errorResponseFrom(MethodArgumentNotValidException exception) {
        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: exception.bindingResult.allErrors.collect(this.&errorFrom)
        )
    }

    private ApiErrorResponse errorResponseFrom(BindException exception) {
        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: exception.bindingResult.allErrors.collect(this.&errorFrom)
        )
    }

    private ApiErrorResponse errorResponseFrom(TypeMismatchException exception) {
        def arguments = [
            value: exception.value,
            propertyName: exception.propertyName,
            requiredType: exception.requiredType.simpleName,
            propertyChangeEvent: exception.propertyChangeEvent
        ]

        def codes = [exception.errorCode, "$exception.errorCode.$exception.propertyName"]

        def apiError = new ApiError(
            code: exception.errorCode,
            codes: codes,
            arguments: arguments,
            defaultMessage: messageResolverService.getMessage(TYPE_MISMATCH, [exception.propertyName, exception.requiredType.simpleName])
        )

        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: [apiError]
        )
    }

    private ApiErrorResponse errorResponseFrom(MissingServletRequestPartException exception) {
        def code = "$MISSING_SERVLET_REQUEST_PART.$exception.requestPartName"
        def codes = [code, MISSING_SERVLET_REQUEST_PART]

        def apiError = new ApiError(
            code: code,
            codes: codes,
            defaultMessage: messageResolverService.getMessage(MISSING_SERVLET_REQUEST_PART, [exception.requestPartName]),
            arguments: [requestPartName: exception.requestPartName]
        )

        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: [apiError]
        )
    }

    private ApiErrorResponse errorResponseFrom(MissingServletRequestParameterException exception) {
        def code = "$MISSING_SERVLET_REQUEST_PARAMETER.$exception.parameterName"
        def codes = [code, MISSING_SERVLET_REQUEST_PARAMETER]

        def apiError = new ApiError(
            code: code,
            codes: codes,
            defaultMessage: messageResolverService.getMessage(MISSING_SERVLET_REQUEST_PARAMETER, [exception.parameterName, exception.parameterType]),
            arguments: [parameterName: exception.parameterName, parameterType: exception.parameterType]
        )

        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: [apiError]
        )
    }

    private ApiErrorResponse errorResponseFrom(MethodArgumentTypeMismatchException exception) {
        def code = "$METHOD_ARGUMENT_TYPE_MISMATCH.$exception.name.$exception.parameter"
        def codes = [code, "$METHOD_ARGUMENT_TYPE_MISMATCH.$exception.name", METHOD_ARGUMENT_TYPE_MISMATCH]

        def errorResponse = errorResponseFrom(exception as TypeMismatchException)

        errorResponse.errors.each {
            it.code = code
            it.codes = codes
            it.defaultMessage = messageResolverService.getMessage(code, [exception.parameter, exception.requiredType.simpleName, exception.name])
            it.arguments << [name: exception.name, parameter: exception.parameter]
        }

        errorResponse
    }

    private ApiErrorResponse errorResponseFrom(ConstraintViolationException exception) {
        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: exception.constraintViolations.collect(this.&errorFrom)
        )
    }

    private ApiErrorResponse errorResponseFrom(DataIntegrityViolationException exception) {
        def apiError = errorFrom(exception.cause)

        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: [apiError]
        )
    }

    private ApiErrorResponse errorResponseFrom(EntityNotFoundException exception) {
        def code = "$ENTITY_NOT_FOUND.$exception.type.simpleName"
        def codes = [code, ENTITY_NOT_FOUND]

        def apiError = new ApiError(
            code: code,
            codes: codes,
            defaultMessage: messageResolverService.getMessage(ENTITY_NOT_FOUND, [exception.type.simpleName, exception.identifier]),
            arguments: [type: exception.type.simpleName, identifier: exception.identifier]
        )

        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: [apiError]
        )
    }

    private ApiErrorResponse errorResponseFrom(ObjectValidationException exception) {
        new ApiErrorResponse(
            status: HttpStatus.BAD_REQUEST,
            message: exception.message,
            errors: exception.fieldErrors
        )
    }

    // 404
    private ApiErrorResponse errorResponseFrom(NoHandlerFoundException exception) {
        def apiError = new ApiError(
            code: NO_HANDLER_FOUND,
            codes: [NO_HANDLER_FOUND],
            defaultMessage: messageResolverService.getMessage(NO_HANDLER_FOUND, [exception.httpMethod, exception.requestURL]),
            arguments: [httpMethod: exception.httpMethod, requestURL: exception.requestURL]
        )

        new ApiErrorResponse(
            status: HttpStatus.NOT_FOUND,
            message: exception.message,
            errors: [apiError]
        )
    }

    // 405
    private ApiErrorResponse errorResponseFrom(HttpRequestMethodNotSupportedException exception) {
        def code = "$HTTP_REQUEST_METHOD_NOT_SUPPORTED.$exception.method"
        def codes = [code, HTTP_REQUEST_METHOD_NOT_SUPPORTED]
        def allowedMethods = exception.supportedMethods.join(', ')

        def apiError = new ApiError(
            code: code,
            codes: codes,
            defaultMessage: messageResolverService.getMessage(HTTP_REQUEST_METHOD_NOT_SUPPORTED, [exception.method, allowedMethods]),
            arguments: [method: exception.method, supportedMethods: exception.supportedMethods]
        )

        new ApiErrorResponse(
            status: HttpStatus.METHOD_NOT_ALLOWED,
            message: exception.message,
            errors: [apiError]
        )
    }

    // 415
    private ApiErrorResponse errorResponseFrom(HttpMediaTypeNotSupportedException exception) {
        def code = "$HTTP_MEDIA_TYPE_NOT_SUPPORTED.$exception.contentType"
        def codes = [code, HTTP_REQUEST_METHOD_NOT_SUPPORTED]
        def allowedMediaTypes = exception.supportedMediaTypes.collect { it.toString() }.join(', ')

        def apiError = new ApiError(
            code: code,
            codes: codes,
            defaultMessage: messageResolverService.getMessage(HTTP_MEDIA_TYPE_NOT_SUPPORTED, [exception.contentType, allowedMediaTypes]),
            arguments: [contentType: exception.contentType, supportedMethods: exception.supportedMediaTypes]
        )

        new ApiErrorResponse(
            status: HttpStatus.METHOD_NOT_ALLOWED,
            message: exception.message,
            errors: [apiError]
        )
    }

    // 500
    private ApiErrorResponse errorResponseFrom(Exception exception) {
        new ApiErrorResponse(
            status: HttpStatus.INTERNAL_SERVER_ERROR,
            message: exception.message
        )
    }

    private ApiError errorFrom(org.hibernate.exception.ConstraintViolationException exception) {
        new ApiError(
            code: exception.errorCode,
            arguments: [constraintName: exception.constraintName],
            defaultMessage: exception.message
        )
    }

    private ApiError errorFrom(ConstraintViolation violation) {
        def baseCode = 'constraintViolation'
        def code = "$baseCode.${violation.rootBeanClass.simpleName}.${violation.leafBean.class.simpleName}.$violation.propertyPath"

        def codes = [code,
            "$baseCode.${violation.rootBeanClass.simpleName}.${violation.leafBean.class.simpleName}.$violation.propertyPath",
            "$baseCode.${violation.rootBeanClass.simpleName}.$violation.propertyPath",
            "$baseCode.${violation.leafBean.class.simpleName}.$violation.propertyPath",
            "$baseCode.$violation.propertyPath"
        ]

        new ApiError(
            code: code,
            codes: codes,
            defaultMessage: violation.message
        )
    }

    private ApiError errorFrom(ObjectError error) {
        def apiError = new ApiError(
            code: error.code,
            codes: error.codes,
            arguments: [objectName: error.objectName, arguments: error.arguments],
            defaultMessage: error.defaultMessage
        )

        apiError
    }

    private ApiError errorFrom(FieldError error) {
        def apiError = errorFrom(error as ObjectError)
        apiError.arguments << [field: error.field, rejectedValue: error.rejectedValue, bindingFailure: error.bindingFailure]
        apiError
    }

    private ApiErrorResponse convertGstringsToString(ApiErrorResponse apiErrorResponse) {
        apiErrorResponse.message = apiErrorResponse.message.toString()
        apiErrorResponse.errors.each {
            it.code = it.code.toString()
            it.codes = it.codes.collect { it.toString() }
        }
        apiErrorResponse
    }
}
