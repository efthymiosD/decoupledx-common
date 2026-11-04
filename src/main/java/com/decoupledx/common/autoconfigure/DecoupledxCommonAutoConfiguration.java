package com.decoupledx.common.autoconfigure;

import com.decoupledx.common.config.ClockConfig;
import com.decoupledx.common.config.OpenApiConfig;
import com.decoupledx.common.error.GlobalExceptionHandler;
import com.decoupledx.common.event.DomainEventPublisher;
import com.decoupledx.common.event.SpringDomainEventPublisher;
import com.decoupledx.common.security.JwtDecoderConfig;
import com.decoupledx.common.security.KeycloakLogoutSuccessHandler;
import com.decoupledx.common.transaction.SpringTransactionRunner;
import com.decoupledx.common.transaction.TransactionRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Registers the decoupledx-common seams: JWT decoder, fixed clock, OpenAPI
 * customization (conditional), REST error handling, the domain-event
 * publisher seam and the transaction runner. Every bean is overridable
 * (@ConditionalOnMissingBean); security ROUTING policy (SecurityConfig)
 * remains application-owned.
 */
@AutoConfiguration
@Import({JwtDecoderConfig.class, ClockConfig.class, OpenApiConfig.class, GlobalExceptionHandler.class})
public class DecoupledxCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DomainEventPublisher.class)
    DomainEventPublisher domainEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisher(publisher);
    }

    @Bean
    @ConditionalOnMissingBean(TransactionRunner.class)
    TransactionRunner transactionRunner(PlatformTransactionManager transactionManager) {
        return new SpringTransactionRunner(transactionManager);
    }

    @Bean
    @ConditionalOnMissingBean
    KeycloakLogoutSuccessHandler keycloakLogoutSuccessHandler(
            @Value("${app.security.end-session-uri}") String endSessionUri) {
        return new KeycloakLogoutSuccessHandler(endSessionUri);
    }
}
