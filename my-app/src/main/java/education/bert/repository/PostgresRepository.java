package education.bert.repository;

import education.bert.exception.DataAccessException;
import education.bert.model.UserModel;
import education.bert.util.jdbc.JdbcHelper;

import java.sql.SQLException;
import java.util.Optional;

public class PostgresRepository {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setup() {
        try {
            JdbcHelper.executeUpdate(url, "DROP TABLE IF EXISTS users;");
            JdbcHelper.executeUpdate(url, "DROP TABLE IF EXISTS posts;");
            JdbcHelper.executeUpdate(url, "CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT NOT NULL);");
            JdbcHelper.executeUpdate(url, "CREATE TABLE posts (id SERIAL PRIMARY KEY, postName TEXT NOT NULL, creatorId INTEGER);");
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public UserModel saveUser(UserModel user) {
        try {
            if (user.getId() == 0) {
                int id = JdbcHelper.executeUpdateWithId(url, "INSERT INTO users(name) VALUES (?);", statement -> {
                    statement.setString(1, user.getName());
                    return statement;
                });
                user.setId(id);
            } else {
                JdbcHelper.executeUpdate(url, "UPDATE users SET name = ? WHERE id = ?;", statement -> {
                    statement.setString(1, user.getName());
                    statement.setInt(2, user.getId());
                    return statement;
                });
            }
            return user;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public Optional<UserModel> getUser(int id) {
        try {
            return JdbcHelper.executeQueryForObject(url, "SELECT id, name FROM users WHERE id = ?;", statement -> {
                statement.setInt(1, id);
                return statement;
            }, resultSet -> new UserModel(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            ));
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
