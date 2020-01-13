package education.bert.util.jdbc;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlCallable<V> {
    V call() throws SQLException;
}
