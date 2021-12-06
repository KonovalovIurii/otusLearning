package ru.otus.core.sessionmanager;

import org.hibernate.SessionFactory;

import java.util.concurrent.Callable;

public class TransactionManagerHibernate implements TransactionManager {
    private final SessionFactory sessionFactory;

    public TransactionManagerHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T> T doInTransaction(TransactionAction<T> action) {
        return wrapException(() -> {
            try (var session = sessionFactory.openSession()) {

                var transaction = session.beginTransaction();
                try {
                    var result = action.apply(session);
                    transaction.commit();
                    return result;
                } catch (Exception ex) {
                    transaction.rollback();
                    throw ex;
                }
            }
        });
    }

    @Override
    public <T> T doInTransactionReadonly(TransactionAction<T> action) {
        return wrapException(() -> {
            try (var session = sessionFactory.openSession()) {
                try {
                    var result = action.apply(session);
                    return result;
                } catch (Exception ex) {
                    throw ex;
                }
            }
        });
    }


    private <T> T wrapException(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception ex) {
            throw new DataBaseOperationException("Exception in transaction", ex);
        }
    }
}
