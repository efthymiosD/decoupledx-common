package com.decoupledx.common.error;

/**
 * The application-agnostic error contract (carried over from the Reservation
 * System's conventions, generalized here). Each application owns its OWN enum
 * implementing this contract, so error codes stay tied to that application's
 * real domain rules. `BusinessException` and `GlobalExceptionHandler` speak
 * only this contract.
 *
 * <p>Enums satisfy {@link #name()} automatically (java.lang.Enum#name).</p>
 */
public interface ErrorCode {

    /** HTTP status that maps to this code class. */
    int httpStatus();

    /** Self-contained, i18n-dictionary-friendly default message. */
    String defaultMessage();

    /** Stable identifier of the code; provided by enums automatically. */
    String name();
}
