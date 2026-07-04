package incident_reporting;

public interface TransactionManager {

	<T> T doInTransaction(UserTransactionCode<T> code);

}
