package net.dlcruz.finance.api.model

import org.springframework.http.HttpStatus

class ApiErrorResponse {

    HttpStatus status
    String message
    List<ApiError> errors
}
