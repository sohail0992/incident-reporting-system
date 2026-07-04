package incident_reporting;

import java.util.function.Function;

@FunctionalInterface
public interface UserTransactionCode<T> extends Function<UserRepository, T> {

}
