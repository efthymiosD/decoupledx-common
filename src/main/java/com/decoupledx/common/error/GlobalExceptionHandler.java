package com.decoupledx.common.error;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST-only error handling (JSON ProblemDetail), scoped to @RestController
 * beans so exceptions from the Thymeleaf web UI fall through to Boot's /error
 * page rendering (HTML error pages) instead of leaking JSON to browsers.
 *
 * <p>The ProblemDetail URN namespace is a property
 * ({@code app.common.problem-urn-base}, default {@code urn:decoupledx:error}),
 * so each application keeps a stable, own URN space for its error codes.</p>
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private final String urnBase;

    GlobalExceptionHandler(@Value("${app.common.problem-urn-base:urn:decoupledx:error}") String urnBase) {
        this.urnBase = urnBase;
    }

    @ExceptionHandler(BusinessException.class)
    ProblemDetail handleBusinessException(BusinessException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(exception.errorCode().httpStatus());
        problem.setType(URI.create(urnBase + ":" + exception.errorCode().name().toLowerCase()));
        problem.setTitle(exception.errorCode().name());
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(422);
        problem.setTitle("VALIDATION_FAILED");
        problem.setDetail(exception.getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce((left, right) -> left + "; " + right)
                .orElse("Request validation failed"));
        return problem;
    }
}
