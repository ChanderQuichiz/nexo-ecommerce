package com.nexo.ecommerce.orders.application.ports;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T executeInTransaction(Supplier<T> callback);
}
