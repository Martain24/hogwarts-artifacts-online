package com.mvilaboa.hogwarts_artifacts_online.system.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;
import com.mvilaboa.hogwarts_artifacts_online.wizard.exception.WizardNameAlreadyInDbException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ObjectNotFoundException.class})
    ResponseEntity<Result<String>> handleNotFoundInDbExceptions(Exception ex) {
        Result<String> resultToSend = new Result<>(false, StatusCode.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultToSend);
    }

    @ExceptionHandler({
            WizardNameAlreadyInDbException.class
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
}
