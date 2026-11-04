package com.decoupledx.common

import com.decoupledx.common.event.DomainEventPublisher
import com.decoupledx.common.security.KeycloakLogoutSuccessHandler
import com.decoupledx.common.transaction.TransactionRunner
import java.time.Clock
import java.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.JwtDecoder
import spock.lang.Specification

/**
 * Proves the library's auto-configuration registers every seam in a plain
 * Spring Boot context: JwtDecoder (dev-key fallback), fixed Clock,
 * TransactionRunner, DomainEventPublisher, KeycloakLogoutSuccessHandler.
 */
@SpringBootTest(classes = TestApp.class, properties = [
        "app.clock.fixed-instant=2026-01-01T00:00:00Z",
        "app.security.end-session-uri=http://localhost:8081/realms/test/logout",
        "app.security.dev-public-key=classpath:dev/jwt-public.pem"
])
class AutoConfigurationContextSpec extends Specification {

    @Autowired
    JwtDecoder jwtDecoder

    @Autowired
    Clock clock

    @Autowired
    TransactionRunner transactionRunner

    @Autowired
    DomainEventPublisher domainEventPublisher

    @Autowired
    KeycloakLogoutSuccessHandler keycloakLogoutSuccessHandler

    def "all shared seams are registered as beans"() {
        expect:
        jwtDecoder != null
        transactionRunner != null
        domainEventPublisher != null
        keycloakLogoutSuccessHandler != null
        Instant.now(clock) == Instant.parse("2026-01-01T00:00:00Z")
    }
}
