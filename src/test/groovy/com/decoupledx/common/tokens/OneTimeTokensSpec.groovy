package com.decoupledx.common.tokens

import spock.lang.Specification

/**
 * Token mechanics: generation entropy/shape and deterministic hash-only
 * storage. The race semantics live in the applications' persistence
 * adapters (SingleUseTokenLedger contract) — covered by their integration
 * specs, not here.
 */
class OneTimeTokensSpec extends Specification {

    def "generated tokens are URL-safe, 22 chars and unique"() {
        when:
        def tokens = (1..100).collect { OneTimeTokens.generate() }
        then:
        tokens.every { it.length() == 22 }
        tokens.every { it ==~ /^[A-Za-z0-9_-]+$/ } // base64url, no padding
        new HashSet<>(tokens).size() == 100
    }

    def "hash is deterministic, 64-char lowercase hex, raw token never recoverable"() {
        given:
        def raw = OneTimeTokens.generate()
        expect:
        OneTimeTokens.hash(raw) == OneTimeTokens.hash(raw)
        OneTimeTokens.hash(raw) ==~ /^[0-9a-f]{64}$/
        and: "different tokens hash differently (no collisions in practice)"
        OneTimeTokens.hash(OneTimeTokens.generate()) != OneTimeTokens.hash(raw)
        and: "the hash does not contain the raw token"
        !OneTimeTokens.hash(raw).contains(raw)
    }

    def "hash rejects a null raw token"() {
        when:
        OneTimeTokens.hash(null)
        then:
        thrown(NullPointerException)
    }
}
