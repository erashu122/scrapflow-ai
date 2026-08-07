package com.scrapflow.identity.api;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail validation(MethodArgumentNotValidException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "One or more fields are invalid.");
    problem.setTitle("Validation failed"); problem.setType(URI.create("https://scrapflow.ai/problems/validation"));
    problem.setProperty("errors", exception.getBindingResult().getFieldErrors().stream().map(error -> java.util.Map.of("field", error.getField(), "message", error.getDefaultMessage())).toList());
    return problem;
  }
}
