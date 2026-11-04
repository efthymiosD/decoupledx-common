package com.decoupledx.common.transaction;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Spring-backed transaction boundary. Read operations run in a read-only
 * transaction; writes of an enclosing transaction simply join it
 * (PROPAGATION_REQUIRED).
 *
 * <p><b>Semantics (verified against Spring 7 bytecode):</b> an exception
 * CROSSING {@code run(...)} always rolls the transaction back —
 * {@code TransactionTemplate#rollbackOnException} rolls back unconditionally
 * and does not consult rollback rules. The decoupledx constraint-race
 * convention therefore puts race resolution INSIDE the work: persistence
 * adapters catch {@code DataIntegrityViolationException} there (e.g. a
 * duplicate-key provision race resolves to the existing row) so the work
 * completes and commits. Never let the DIVE cross the boundary.</p>
 */
public class SpringTransactionRunner implements TransactionRunner {

    private final TransactionTemplate transactionTemplate;
    private final TransactionTemplate readOnlyTransactionTemplate;

    @Autowired
    public SpringTransactionRunner(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTransactionTemplate.setReadOnly(true);
    }

    @Override
    public <T> T run(Supplier<T> work) {
        return transactionTemplate.execute(_ -> work.get());
    }

    @Override
    public void run(Runnable work) {
        transactionTemplate.executeWithoutResult(_ -> work.run());
    }

    @Override
    public <T> T runReadOnly(Supplier<T> work) {
        return readOnlyTransactionTemplate.execute(_ -> work.get());
    }
}
