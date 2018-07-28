package net.dlcruz.finance.api.exception

import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import javax.validation.ConstraintViolationException

@ControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ApiErrorTransformerService apiErrorService

    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        handleExceptionInternal(ex, errorResponse, headers, errorResponse.status, request)
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        handleExceptionInternal(ex, errorResponse, headers, errorResponse.status, request)
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException)
    ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }

    @ExceptionHandler(ConstraintViolationException)
    ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex, final WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }

    @ExceptionHandler(DataIntegrityViolationException)
    ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException ex, final WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }

    @ExceptionHandler(EntityNotFoundException)
    ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException ex, final WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }

    @ExceptionHandler(ObjectValidationException)
    ResponseEntity<Object> handleEntityNotFoundException(final ObjectValidationException ex, final WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }

    // 404
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    // 405
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    // 415
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, headers, errorResponse.status)
    }

    // 500
    @ExceptionHandler(Exception)
    ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {

        logger.error(ex.message, ex)

        def errorResponse = apiErrorService.getErrorResponseFor(ex)
        return new ResponseEntity<Object>(errorResponse, new HttpHeaders(), errorResponse.status)
    }
}
