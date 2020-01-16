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
    private String dbUrl;

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
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
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS users;");
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS posts;");
            JdbcHelper.executeUpdate(
                    dbUrl,
                    "CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT NOT NULL);"
            );
            JdbcHelper.executeUpdate(
                    dbUrl,
                    "CREATE TABLE posts (id SERIAL PRIMARY KEY, postName TEXT NOT NULL, creatorId INTEGER);"
            );
        });
    }

    public void dropTables() {
        sqlRun(() ->
        {
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS users;");
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS posts;");
        });
    }

    public UserModel saveUser(UserModel user) {
        return sqlCall(() ->
                {
                    UserModel result = new UserModel(user.getId(), user.getName());
                    if (user.getId() == 0) {
                        int id = JdbcHelper.executeUpdateWithId(
                                dbUrl,
                                "INSERT INTO users(name) VALUES (?);",
                                statement ->
                                {
                                    statement.setString(1, user.getName());
                                    return statement;
                                }
                        );
                        result.setId(id);
                    } else if (0 ==
                            JdbcHelper.executeUpdate(
                                    dbUrl,
                                    "UPDATE users SET name = ? WHERE id = ?;",
                                    statement ->
                                    {
                                        statement.setString(1, user.getName());
                                        statement.setInt(2, user.getId());
                                        return statement;
                                    }
                            )
                    ) {
                        return null;
                    }
                    return result;
                }
        );
    }

    public UserModel getUser(int id) {
        return sqlCall(() ->
                JdbcHelper.executeQueryForObject(
                        dbUrl,
                        "SELECT id, name FROM users WHERE id = ?;",
                        statement ->
                        {
                            statement.setInt(1, id);
                            return statement;
                        },
                        resultSet -> new UserModel(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        )
                )
        ).orElse(null);
    }

    public boolean removeUser(int id) {
        return sqlCall(() ->
                0 != JdbcHelper.executeUpdate(
                        dbUrl,
                        "DELETE FROM users WHERE id = ?;",
                        statement ->
                        {
                            statement.setInt(1, id);
                            return statement;
                        }
                )
        );
    }

    public int getUsersCount() {
        return sqlCall(() ->
                {
                    Optional<Integer> result = JdbcHelper.executeQueryForObject(
                            dbUrl,
                            "SELECT COUNT(*) AS cnt FROM users;",
                            statement -> statement,
                            resultSet -> resultSet.getInt("cnt")
                    );
                    return result.orElse(0);
                }
        );
    }

    public PostModel savePost(PostModel post) {
        return sqlCall(() ->
                {
                    PostModel result = new PostModel(post.getId(), post.getPostName(), post.getCreatorId());
                    if (post.getId() == 0) {
                        int id = JdbcHelper.executeUpdateWithId(
                                dbUrl,
                                "INSERT INTO posts(postName, creatorId) VALUES (?, ?);",
                                statement ->
                                {
                                    statement.setString(1, post.getPostName());
                                    statement.setInt(2, post.getCreatorId());
                                    return statement;
                                }
                        );
                        result.setId(id);
                    } else if (0 ==
                            JdbcHelper.executeUpdate(
                                    dbUrl,
                                    "UPDATE posts SET postName = ?, creatorId = ? WHERE id = ?;",
                                    statement ->
                                    {
                                        statement.setString(1, post.getPostName());
                                        statement.setInt(2, post.getCreatorId());
                                        statement.setInt(3, post.getId());
                                        return statement;
                                    }
                            )
                    ) {
                        return null;
                    }
                    return result;
                }
        );
    }

    public PostModel getPost(int id) {
        return sqlCall(() ->
                JdbcHelper.executeQueryForObject(
                        dbUrl,
                        "SELECT id, postName, creatorId FROM posts WHERE id = ?;",
                        statement ->
                        {
                            statement.setInt(1, id);
                            return statement;
                        },
                        resultSet -> new PostModel(
                                resultSet.getInt("id"),
                                resultSet.getString("postName"),
                                resultSet.getInt("creatorId")
                        )
                )
        ).orElse(null);
    }

    public boolean removePost(int id) {
        return sqlCall(() ->
                0 != JdbcHelper.executeUpdate(
                        dbUrl,
                        "DELETE FROM posts WHERE id = ?;",
                        statement ->
                        {
                            statement.setInt(1, id);
                            return statement;
                        }
                )
        );
    }

    public int getPostsCount() {
        return sqlCall(() ->
                {
                    Optional<Integer> result = JdbcHelper.executeQueryForObject(
                            dbUrl,
                            "SELECT COUNT(*) AS cnt FROM posts;",
                            statement -> statement,
                            resultSet -> resultSet.getInt("cnt")
                    );
                    return result.orElse(0);
                }
        );
    }

    public int getPostsCountForCreator(int creatorId) {
        return sqlCall(() ->
                {
                    Optional<Integer> result = JdbcHelper.executeQueryForObject(
                            dbUrl,
                            "SELECT COUNT(*) AS cnt FROM posts WHERE creatorId = ?;",
                            statement ->
                            {
                                statement.setInt(1, creatorId);
                                return statement;
                            },
                            resultSet -> resultSet.getInt("cnt")
                    );
                    return result.orElse(0);
                }
        );
    }
}
