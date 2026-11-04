package com.decoupledx.common.event

import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

class SpringDomainEventPublisherSpec extends Specification {

    static class SampleEvent implements DomainEvent {}

    def "delegates the domain event to Spring's publisher"() {
        given:
        def spring = Mock(ApplicationEventPublisher)
        def publisher = new SpringDomainEventPublisher(spring)
        def event = new SampleEvent()
        when:
        publisher.publish(event)
        then:
        1 * spring.publishEvent(event)
    }
}
