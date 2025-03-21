package com.mvilaboa.hogwarts_artifacts_online.system.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ ObjectNotFoundException.class })
    ResponseEntity<Result<String>> handleNotFoundInDbExceptions(Exception ex) {
        Result<String> resultToSend = new Result<>(false, StatusCode.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultToSend);
    }

    @ExceptionHandler({
            AlreadyInDbException.class
    })
    ResponseEntity<Result<String>> handleAlreadyInDbExceptions(Exception ex) {
        Result<String> resultToSend = new Result<>(false, StatusCode.INVALID_ARGUMENT, ex.getMessage());
        return ResponseEntity.status(StatusCode.INVALID_ARGUMENT).body(resultToSend);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    ResponseEntity<Result<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult().getAllErrors()
                .stream().map(e -> ((FieldError) e)).toList();

        Map<String, String> jsonData = new HashMap<>();

        errors.forEach((error) -> {
            String key = error.getField();
            String val = error.getDefaultMessage();
            jsonData.put(key, val);
        });

        Result<Map<String, String>> resultToSend = new Result<>(
                false,
                StatusCode.INVALID_ARGUMENT,
                "Provided arguments are invalid",
                jsonData);

        return ResponseEntity.badRequest().body(resultToSend);
    }

    @ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    ResponseEntity<Result<String>> handleAuthenticationException(Exception ex) {
        Result<String> resultToSend = new Result<>(
                false, StatusCode.UNAUTHORIZED, "username or password is incorrect");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultToSend);
    }

    @ExceptionHandler({ AccountStatusException.class })
    ResponseEntity<Result<String>> handleAccountStatusException(Exception ex) {
        Result<String> resultToSend = new Result<>(
                false, StatusCode.UNAUTHORIZED, "User account is abnormal");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultToSend);
    }

    @ExceptionHandler({ InvalidBearerTokenException.class })
    ResponseEntity<Result<String>> handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        Result<String> resultToSend = new Result<>(
                false, 
                StatusCode.UNAUTHORIZED, 
                "The access token provided is expired, revoked, malformed or invalid for other reasons.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultToSend);
    }

    @ExceptionHandler({ AccessDeniedException.class })
    ResponseEntity<Result<String>> handleAccessDeniedException(AccessDeniedException ex) {
        Result<String> resultToSend = new Result<>(
                false, 
                StatusCode.UNAUTHORIZED, 
                "No permission.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultToSend);
    }

    @ExceptionHandler({ Exception.class })
    ResponseEntity<Result<String>> handleOtherException(Exception ex) {
        Result<String> resultToSend = new Result<>(
                false, 
                StatusCode.INTERNAL_SERVER_ERROR, 
                "Server internal error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultToSend);
    }


}
