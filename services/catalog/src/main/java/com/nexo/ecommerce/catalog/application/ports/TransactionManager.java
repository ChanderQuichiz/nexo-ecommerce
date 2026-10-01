package com.nexo.ecommerce.catalog.application.ports;

import java.util.function.Supplier;

public interface TransactionManager {

    <T> T executeInTransaction(Supplier<T> operation);
}