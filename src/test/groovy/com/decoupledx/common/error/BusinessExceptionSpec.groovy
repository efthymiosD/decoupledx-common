package com.decoupledx.common.error

import spock.lang.Specification

class BusinessExceptionSpec extends Specification {

    enum TestCode implements ErrorCode {
        SAMPLE_CONFLICT(409, "Sample conflict"),
        SAMPLE_INVALID(422, "Sample invalid")

        final int httpStatus
        final String defaultMessageText

        TestCode(int httpStatus, String defaultMessage) {
            this.httpStatus = httpStatus
            this.defaultMessageText = defaultMessage
        }

        @Override
        int httpStatus() { return httpStatus }

        @Override
        String defaultMessage() { return defaultMessageText }
    }

    def "uses the contract's default message by default"() {
        when:
        def exception = new BusinessException(TestCode.SAMPLE_CONFLICT)
        then:
        exception.errorCode() == TestCode.SAMPLE_CONFLICT
        exception.message == "Sample conflict"
    }

    def "allows an explicit message override"() {
        expect:
        new BusinessException(TestCode.SAMPLE_INVALID, "custom").message == "custom"
    }

    def "contract exposes status via the enum"() {
        expect:
        TestCode.SAMPLE_CONFLICT.httpStatus() == 409
        TestCode.SAMPLE_CONFLICT.name() == "SAMPLE_CONFLICT"
    }
}
