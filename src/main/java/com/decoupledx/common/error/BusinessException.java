package com.decoupledx.common.error;

/**
 * The shared runtime exception for domain rule violations (carried over from
 * decoupledx application conventions). Carries an {@link ErrorCode contract}
 * so adapters translate it to a stable HTTP ProblemDetail; the message is the
 * self-contained default message unless explicitly overridden.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
