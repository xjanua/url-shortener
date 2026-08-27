package me.xjanua.spring.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import me.xjanua.spring.backend.dto.RestResponseError;

@RestControllerAdvice
public class HandleGlobalException {

        @ExceptionHandler({
                        IllegalArgumentException.class,
                        HttpMessageNotReadableException.class,
                        MethodArgumentNotValidException.class,
                        BadRequestException.class
        })
        public ResponseEntity<RestResponseError> handleBadRequest(Exception ex) {
                String code = ErrorCode.INVALID_ARGUMENT;
                String message = ex.getMessage();

                if (ex instanceof MethodArgumentNotValidException e) {
                        code = ErrorCode.VALIDATION_ERROR;
                        message = e.getBindingResult()
                                        .getFieldErrors()
                                        .stream()
                                        .map(f -> f.getDefaultMessage())
                                        .findFirst()
                                        .orElse("Validation error");
                } else if (ex instanceof HttpMessageNotReadableException) {
                        code = ErrorCode.INVALID_REQUEST_BODY;
                } else if (ex instanceof BadRequestException e) {
                        code = e.getCode();
                }

                return ResponseEntity.badRequest()
                                .body(new RestResponseError(code, message));
        }

        @ExceptionHandler({
                        BadCredentialsException.class,
                        UsernameNotFoundException.class
        })
        public ResponseEntity<RestResponseError> handleUnauthorized(Exception ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new RestResponseError(ErrorCode.INVALID_CREDENTIALS, ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<RestResponseError> handleForbidden(Exception ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new RestResponseError(ErrorCode.FORBIDDEN, "Access denied"));
        }

        @ExceptionHandler({ NotFoundException.class, NoResourceFoundException.class })
        public ResponseEntity<RestResponseError> handleNotFound(Exception ex) {
                String code = ex instanceof NotFoundException e
                                ? e.getCode()
                                : ErrorCode.RESOURCE_NOT_FOUND;

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new RestResponseError(code, ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<RestResponseError> handleException(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new RestResponseError(ErrorCode.INTERNAL_SERVER_ERROR,
                                                "Internal server error"));
        }
}
