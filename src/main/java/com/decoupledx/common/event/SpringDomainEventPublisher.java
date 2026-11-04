package com.decoupledx.common.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Spring-backed implementation of the {@link DomainEventPublisher} port — the
 * single infrastructure seam of the event pipeline (mirrors
 * {@code SpringTransactionRunner}). When the Spring Modulith registry starter
 * is active, publications are logged in the enclosing transaction and
 * delivered after commit.
 *
 * <p>Wired by the library's auto-configuration; an application may override
 * the bean to swap the mechanism.</p>
 */
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Autowired
    public SpringDomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
