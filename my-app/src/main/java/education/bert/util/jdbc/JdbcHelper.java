package education.bert.util.jdbc;

import java.sql.*;
import java.util.Optional;

public class JdbcHelper {
    public static int executeUpdate(String url, String sql) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();
        ) {
            return statement.executeUpdate(sql);
        }
    }

    public static int executeUpdate(String url, String sql, PreparedStatementSetter setter) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = setter.set(connection.prepareStatement(sql));
        ) {
            return preparedStatement.executeUpdate();
        }
    }

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
