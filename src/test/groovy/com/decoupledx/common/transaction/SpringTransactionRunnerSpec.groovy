package com.decoupledx.common.transaction

import java.util.function.Supplier
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import spock.lang.Specification

class SpringTransactionRunnerSpec extends Specification {

    def "commits the write transaction and returns the work result"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        when:
        def result = runner.run({ "ok" } as Supplier<String>)
        then:
        1 * ptm.getTransaction(_) >> status
        1 * ptm.commit(status)
        0 * ptm.rollback(_)
        result == "ok"
    }

    def "runs Runnable work on the write template"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        when:
        runner.run({ sideEffect = true })
        then:
        1 * ptm.getTransaction(_) >> status
        1 * ptm.commit(status)
    }

    def "rolls back when the work throws between transactions"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        when:
        runner.run({ throw new IllegalStateException("boom") } as Supplier<String>)
        then:
        1 * ptm.getTransaction(_) >> status
        1 * ptm.rollback(status)
        0 * ptm.commit(_)
        thrown(IllegalStateException)
    }

    def "DataIntegrityViolationException crossing run() also rolls back — race resolution happens INSIDE the work"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        when:
        runner.run({ throw new DataIntegrityViolationException("duplicate") } as Supplier<String>)
        then:
        1 * ptm.getTransaction(_) >> status
        1 * ptm.rollback(status)
        0 * ptm.commit(_)
        thrown(DataIntegrityViolationException)
    }

    def "constraint-race convention resolved inside the work commits normally"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        // persistence-adapter-style work: the race is handled inside, the work completes
        when:
        def result = runner.run({ "existing-row" } as Supplier<String>)
        then:
        1 * ptm.getTransaction(_) >> status
        1 * ptm.commit(status)
        result == "existing-row"
    }

    def "read-only work runs in a read-only transaction"() {
        given:
        def ptm = Mock(PlatformTransactionManager)
        def status = Mock(TransactionStatus)
        def runner = new SpringTransactionRunner(ptm)
        when:
        def result = runner.runReadOnly({ "ro" } as Supplier<String>)
        then:
        1 * ptm.getTransaction({ it.readOnly }) >> status
        1 * ptm.commit(status)
        result == "ro"
    }

    def sideEffect = false
}
