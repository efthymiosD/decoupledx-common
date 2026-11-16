package com.decoupledx.common.tokens;

/**
 * The canonical storage contract for single-use tokens, implemented by each
 * application's persistence adapter over ITS OWN table (the library owns no
 * schema — the token's meaning, foreign keys and minting rights are domain
 * concerns).
 *
 * <p>Implementations persist the HASH only (see {@link OneTimeTokens#hash});
 * the raw token never reaches storage.</p>
 *
 * <p>Race semantics — the reason this contract exists: implementations MUST
 * consume with a single atomic guarded statement
 * ({@code UPDATE … SET used_at = now() WHERE token_hash = ? AND used_at IS
 * NULL}) in its own unit. Under a parallel first-use race exactly one caller
 * matches a row; the loser gets {@code false} and surfaces the application's
 * honest already-used conflict (a 409-class business error, never a 500,
 * never a silent double-use). A lost update race that aborts the Postgres
 * transaction must be resolved INSIDE the implementing work per ADR-0002 —
 * never let the failure cross back into a caller that still holds SQL to
 * run.</p>
 */
public interface SingleUseTokenLedger {

    /**
     * Atomically consumes the token identified by {@code tokenHash}.
     *
     * @return {@code true} if THIS call consumed the token (first use);
     *         {@code false} if it was already consumed. Unknown tokens are a
     *         caller concern (look up existence separately where the flow
     *         needs to distinguish unknown from already-used).
     */
    boolean consume(String tokenHash);
}
