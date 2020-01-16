package education.bert.service;

import education.bert.exception.DataAccessException;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.util.jdbc.JdbcHelper;
import education.bert.util.jdbc.SqlCallable;
import education.bert.util.jdbc.SqlRunnable;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service class that provides read/write data from/to the database. This service implements a simplified model of an
 * internet forum or social network.
 */
public class ForumService {

    /**
     * PostgreSQL database url.
     */
    private String dbUrl;

    /**
     * DB url setter.
     *
     * @param dbUrl a url to be set.
     */
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * The wrapper for code throwing SQLException. The SQLException is replaced by RuntimeException.
     *
     * @param runnable a SqlRunnable functional interface implementation that runs desired code.
     */
    private void sqlRun(SqlRunnable runnable) {
        try {
            runnable.run();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * The wrapper for code returning some value and throwing SQLException. The SQLException is replaced by
     * RuntimeException.
     *
     * @param callable a SqlCallable functional interface implementation that calls some value from code.
     * @param <V>      the type of value returning from the code.
     * @return the same value that code returns.
     */
    private <V> V sqlCall(SqlCallable<V> callable) {
        try {
            return callable.call();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Creates empty DB tables.
     */
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

    /**
     * Drops DB tables.
     */
    public void dropTables() {
        sqlRun(() ->
        {
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS users;");
            JdbcHelper.executeUpdate(dbUrl, "DROP TABLE IF EXISTS posts;");
        });
    }

    /**
     * Adds specified user to DB if {@code user.getId() == 0}, otherwise updates existing user.
     *
     * @param user user to be added or updated.
     * @return either (1) the same user with updated id or (2) {@code null} if there is no user to update in the
     * database.
     */
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

    /**
     * Returns user with specified id from DB.
     *
     * @param id id by which user is to be returned.
     * @return user with specified id from DB.
     */
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

    /**
     * Removes user with specified id from DB.
     *
     * @param id id by which user is to be removed.
     * @return {@code true} if the user is successfully removed, otherwise {@code null}.
     */
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

    /**
     * Returns the total number of users in the database.
     *
     * @return the total number of users in the database.
     */
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

    /**
     * Adds specified post to DB if {@code post.getId() == 0}, otherwise updates existing post.
     *
     * @param post post to be added or updated.
     * @return either (1) the same post with updated id or (2) {@code null} if there is no post to update in the
     * database.
     */
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

    /**
     * Returns post with specified id from DB.
     *
     * @param id id by which post is to be returned.
     * @return post with specified id from DB.
     */
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

    /**
     * Removes post with specified id from DB.
     *
     * @param id id by which post is to be removed.
     * @return {@code true} if the post is successfully removed, otherwise {@code null}.
     */
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

    /**
     * Returns the total number of posts in the database.
     *
     * @return the total number of posts in the database.
     */
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

    /**
     * Returns the number of posts for specified creator id.
     *
     * @param creatorId creator id for which number of posts is to be returned.
     * @return the number of posts for specified creator id.
     */
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
