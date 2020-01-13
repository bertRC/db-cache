package education.bert.service;

import education.bert.exception.DataAccessException;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.util.jdbc.JdbcHelper;
import education.bert.util.jdbc.SqlCallable;
import education.bert.util.jdbc.SqlRunnable;

import java.sql.SQLException;
import java.util.Optional;

public class ForumService {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    private void sqlRun(SqlRunnable runnable) {
        try {
            runnable.run();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private <V> V sqlCall(SqlCallable<V> callable) {
        try {
            return callable.call();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void setup() {
        sqlRun(() ->
        {
            JdbcHelper.executeUpdate(url, "DROP TABLE IF EXISTS users;");
            JdbcHelper.executeUpdate(url, "DROP TABLE IF EXISTS posts;");
            JdbcHelper.executeUpdate(url, "CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT NOT NULL);");
            JdbcHelper.executeUpdate(url, "CREATE TABLE posts (id SERIAL PRIMARY KEY, postName TEXT NOT NULL, creatorId INTEGER);");
        });
    }

    public UserModel saveUser(UserModel user) {
        return sqlCall(() ->
        {
            UserModel result = new UserModel(user.getId(), user.getName());
            if (user.getId() == 0) {
                int id = JdbcHelper.executeUpdateWithId(url, "INSERT INTO users(name) VALUES (?);", statement -> {
                    statement.setString(1, user.getName());
                    return statement;
                });
                result.setId(id);
            } else {
                JdbcHelper.executeUpdate(url, "UPDATE users SET name = ? WHERE id = ?;", statement -> {
                    statement.setString(1, user.getName());
                    statement.setInt(2, user.getId());
                    return statement;
                });
            }
            return result;
        });
    }

    public Optional<UserModel> getUser(int id) {
        return sqlCall(() ->
                JdbcHelper.executeQueryForObject(url, "SELECT id, name FROM users WHERE id = ?;", statement -> {
                    statement.setInt(1, id);
                    return statement;
                }, resultSet -> new UserModel(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                ))
        );
    }

    public void removeUser(int id) {
        sqlRun(() ->
                JdbcHelper.executeUpdate(url, "DELETE FROM users WHERE id = ?;", statement -> {
                    statement.setInt(1, id);
                    return statement;
                })
        );
    }

    public int getUsersCount() {
        return sqlCall(() ->
        {
            Optional<Integer> result = JdbcHelper.executeQueryForObject(url, "SELECT COUNT(*) AS cnt FROM users;",
                    statement -> statement, resultSet -> resultSet.getInt("cnt"));
            return result.orElse(0);
        });
    }

    public PostModel savePost(PostModel post) {
        return sqlCall(() ->
        {
            PostModel result = new PostModel(post.getId(), post.getPostName(), post.getCreatorId());
            if (post.getId() == 0) {
                int id = JdbcHelper.executeUpdateWithId(url, "INSERT INTO posts(postName, creatorId) VALUES (?, ?);",
                        statement -> {
                            statement.setString(1, post.getPostName());
                            statement.setInt(2, post.getCreatorId());
                            return statement;
                        });
                result.setId(id);
            } else {
                JdbcHelper.executeUpdate(url, "UPDATE posts SET postName = ?, creatorId = ? WHERE id = ?;",
                        statement -> {
                            statement.setString(1, post.getPostName());
                            statement.setInt(2, post.getCreatorId());
                            statement.setInt(3, post.getId());
                            return statement;
                        });
            }
            return result;
        });
    }

    public Optional<PostModel> getPost(int id) {
        return sqlCall(() ->
                JdbcHelper.executeQueryForObject(url, "SELECT id, postName, creatorId FROM posts WHERE id = ?;", statement -> {
                    statement.setInt(1, id);
                    return statement;
                }, resultSet -> new PostModel(
                        resultSet.getInt("id"),
                        resultSet.getString("postName"),
                        resultSet.getInt("creatorId")
                ))
        );
    }

    public void removePost(int id) {
        sqlRun(() ->
                JdbcHelper.executeUpdate(url, "DELETE FROM posts WHERE id = ?;", statement -> {
                    statement.setInt(1, id);
                    return statement;
                })
        );
    }

    public int getPostsCount() {
        return sqlCall(() ->
        {
            Optional<Integer> result = JdbcHelper.executeQueryForObject(url, "SELECT COUNT(*) AS cnt FROM posts;",
                    statement -> statement, resultSet -> resultSet.getInt("cnt"));
            return result.orElse(0);
        });
    }

    public int getPostsCountForCreator(int creatorId) {
        return sqlCall(() ->
        {
            Optional<Integer> result = JdbcHelper.executeQueryForObject(url, "SELECT COUNT(*) AS cnt FROM posts WHERE creatorId = ?;",
                    statement -> {
                        statement.setInt(1, creatorId);
                        return statement;
                    }, resultSet -> resultSet.getInt("cnt"));
            return result.orElse(0);
        });
    }
}
