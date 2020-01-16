package education.bert.util.jdbc;

import java.sql.*;
import java.util.Optional;

/**
 * Utility class that makes working with databases easier.
 */
public class JdbcHelper {

    /**
     * The wrapper for java.sql.Statement.executeUpdate with connection establishment and statement creation.
     *
     * @param url a database url.
     * @param sql an SQL Data Manipulation Language (DML) statement.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements
     * that return nothing.
     * @throws SQLException if a database access error occurs.
     */
    public static int executeUpdate(String url, String sql) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
        ) {
            return statement.executeUpdate(sql);
        }
    }

    /**
     * The wrapper for java.sql.PreparedStatement.executeUpdate with connection establishment and preparedStatement
     * creation.
     *
     * @param url    a database url.
     * @param sql    an SQL Data Manipulation Language (DML) statement.
     * @param setter a PreparedStatementSetter functional interface implementation that sets the PreparedStatement.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements
     * that return nothing.
     * @throws SQLException if a database access error occurs.
     */
    public static int executeUpdate(String url, String sql, PreparedStatementSetter setter) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = setter.set(connection.prepareStatement(sql));
        ) {
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * The wrapper for java.sql.PreparedStatement.executeUpdate with connection establishment and preparedStatement
     * creation that returns object containing the auto-generated key.
     *
     * @param url    a database url.
     * @param sql    an SQL Data Manipulation Language (DML) statement.
     * @param setter a PreparedStatementSetter functional interface implementation that sets the PreparedStatement.
     * @param <T>    the type of object containing the auto-generated key.
     * @return the object containing the auto-generated key.
     * @throws SQLException if a database access error occurs.
     */
    public static <T> T executeUpdateWithId(String url, String sql, PreparedStatementSetter setter) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = setter.set(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
        ) {
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return (T) generatedKeys.getObject(1);
            }
            throw new SQLException("No keys generated");
        }
    }

    /**
     * The wrapper for java.sql.PreparedStatement.executeQuery with connection establishment, preparedStatement and
     * resultSet creation.
     *
     * @param url    a database url.
     * @param sql    an SQL Data Manipulation Language (DML) statement.
     * @param setter a PreparedStatementSetter functional interface implementation that sets the PreparedStatement.
     * @param mapper a RowMapper functional interface implementation that maps ResultSet to desired object.
     * @param <T>    the type of object queried from the database.
     * @return Optional container for the object queried from the database.
     * @throws SQLException if a database access error occurs.
     */
    public static <T> Optional<T> executeQueryForObject(String url, String sql, PreparedStatementSetter setter, RowMapper<T> mapper) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = setter.set(connection.prepareStatement(sql));
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            if (resultSet.next()) {
                return Optional.of(mapper.map(resultSet));
            }
            return Optional.empty();
        }
    }
}
