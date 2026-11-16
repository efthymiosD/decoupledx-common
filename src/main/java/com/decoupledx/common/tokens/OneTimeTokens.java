package com.decoupledx.common.tokens;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;

/**
 * One-time token mechanics shared by the decoupledx applications: secure
 * generation, opaque URLs, and hash-only storage.
 *
 * <p>Raw tokens are the ONLY representation that may appear in a link or an
 * API response — exactly once, at mint time. Persisted state must hold the
 * SHA-256 hash only (the applications' security checklist: "tokens opaque +
 * only hashes stored"); a database leak then reveals nothing usable.</p>
 *
 * <p>Consumption semantics (the part worth standardizing — see ADR-0003):
 * the consuming write MUST be a single atomic guarded statement
 * ({@code UPDATE … SET used_at = now() WHERE token_hash = ? AND used_at IS
 * NULL}), run in its own unit. Under a parallel first-use race exactly one
 * caller's UPDATE matches a row; the loser matches 0 rows and surfaces the
 * application's honest already-used/unknown conflict (never a 500, never a
 * silent double-use). Persistence-level races (version/deadlock) follow
 * ADR-0002: they always roll back the wrapping transaction and are
 * translated by the application's persistence adapter.</p>
 */
public final class OneTimeTokens {

    /** 128 bits of entropy — enough that minted links are unguessable. */
    private static final int TOKEN_BYTES = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private OneTimeTokens() {
    }

    /**
     * A fresh raw token: 22-char unpadded base64url (128 bits of entropy) —
     * safe to embed in a URL path or a JSON body, unambiguous in logs.
     */
    public static String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** SHA-256 of the raw token, lowercase hex — the ONLY value to persist. */
    public static String hash(String rawToken) {
        Objects.requireNonNull(rawToken, "raw token must not be null");
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException impossibleOnJdk) {
            throw new IllegalStateException("SHA-256 unavailable", impossibleOnJdk);
        }
    }
}
