package education.bert.util.jdbc;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlRunnable {
    void run() throws SQLException;
}
