package com.example.demo.shared;

import com.example.demo.customer.CustomerNotFoundException;
import com.example.demo.customer.DuplicateEmailException;
import com.example.demo.customer.DuplicateGithubUsernameException;
import com.example.demo.integration.github.GitHubIntegrationException;
import com.example.demo.integration.github.GitHubUserNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateGithubUsernameException.class)
    public ResponseEntity<ApiErrorResponse> handleGithubConflict(DuplicateGithubUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(GitHubUserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGithubUserNotFound(GitHubUserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(GitHubIntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleGithubIntegration(GitHubIntegrationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
            .map(this::formatError)
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(details));
    }

    private String formatError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
