package com.msohailse.app.incident;

public interface TransactionManager {
	<T> T doInTransaction(TransactionCode<T> code);
}
