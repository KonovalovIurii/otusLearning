package ru.otus.core.sessionmanager;

public interface TransactionManager {

    <T> T doInTransaction(TransactionAction<T> action);

    <T> T doInTransactionReadonly(TransactionAction<T> action);
}
