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
                String message = ex.getMessage();

                if (ex instanceof MethodArgumentNotValidException e) {
                        message = e.getBindingResult()
                                        .getFieldErrors()
                                        .stream()
                                        .map(f -> f.getDefaultMessage())
                                        .findFirst()
                                        .orElse("Validation error");
                }

                return ResponseEntity.badRequest()
                                .body(new RestResponseError(message));
        }

        @ExceptionHandler({
                        BadCredentialsException.class,
                        UsernameNotFoundException.class
        })
        public ResponseEntity<RestResponseError> handleUnauthorized(Exception ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new RestResponseError(ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<RestResponseError> handleForbidden(Exception ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new RestResponseError("Access denied"));
        }

        @ExceptionHandler({ NotFoundException.class, NoResourceFoundException.class })
        public ResponseEntity<RestResponseError> handleNotFound(Exception ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new RestResponseError(ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<RestResponseError> handleException(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new RestResponseError("Internal server error"));
        }
}
