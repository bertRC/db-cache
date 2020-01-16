package education.bert.unit;

import education.bert.PostgresConfig;
import education.bert.benchmark.ForumServiceBenchmark;
import education.bert.exception.DataAccessException;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.service.CachedForumService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CachedForumServiceTest extends AForumServiceTest {
    private final CachedForumService service = new CachedForumService();

    {
        service.setDbUrl(PostgresConfig.url);
        super.setService(service);
    }

    @AfterAll
    static void benchmarkRunner() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ForumServiceBenchmark.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .build();
        new Runner(options).run();
    }

    @BeforeEach
    @Override
    public void setup() {
        service.setup(3);
    }

    @Test
    public void getUserFromCacheTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasya");

        service.saveUser(user);
        UserModel firstGetUser = service.getUser(1);
        assertEquals(expectedUser, firstGetUser);

        service.dropTables();

        UserModel secondGetUser = service.getUser(1);
        assertEquals(expectedUser, secondGetUser);

    }

    @Test
    public void getPostFromCacheTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);

        service.savePost(post);
        PostModel firstGetPost = service.getPost(1);
        assertEquals(expectedPost, firstGetPost);

        service.dropTables();

        PostModel secondGetPost = service.getPost(1);
        assertEquals(expectedPost, secondGetPost);
    }

    @Test
    public void getPostsCountForCreatorFromCacheTest() {
        super.getPostsCountForCreatorTest();

        service.dropTables();

        assertEquals(2, service.getPostsCountForCreator(1));
    }

    @Test
    public void saveUserShouldInvalidateGetUsersCountTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel newUser = new UserModel(0, "Petya");

        service.saveUser(user);
        assertEquals(1, service.getUsersCount());

        service.saveUser(newUser);
        service.dropTables();

        assertThrows(DataAccessException.class, service::getUsersCount);
    }

    @Test
    public void updateUserShouldInvalidateGetUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel savedUser = new UserModel(1, "Vasya");
        UserModel updatedUser = new UserModel(1, "Vasiliy");

        service.saveUser(user);
        assertEquals(savedUser, service.getUser(1));

        service.saveUser(updatedUser);
        service.dropTables();

        assertThrows(DataAccessException.class, () -> service.getUser(1));
    }

    @Test
    public void removeUserShouldInvalidateGetUserAndCountTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel savedUser = new UserModel(1, "Vasya");

        service.saveUser(user);
        assertEquals(savedUser, service.getUser(1));
        assertEquals(1, service.getUsersCount());

        service.removeUser(1);
        service.dropTables();

        assertThrows(DataAccessException.class, () -> service.getUser(1));
        assertThrows(DataAccessException.class, service::getUsersCount);
    }

    @Test
    public void savePostShouldInvalidateGetPostsCountTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel newPost = new PostModel(0, "Forum Rules", 2);

        service.savePost(post);
        assertEquals(1, service.getPostsCount());
        assertEquals(0, service.getPostsCountForCreator(2));

        service.savePost(newPost);
        service.dropTables();

        assertThrows(DataAccessException.class, service::getPostsCount);
        assertThrows(DataAccessException.class, () -> service.getPostsCountForCreator(2));
    }

    @Test
    public void updatePostShouldInvalidateGetPostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel savedPost = new PostModel(1, "Hello Friends", 1);
        PostModel updatedPost = new PostModel(1, "Hello Folks", 1);

        service.savePost(post);
        assertEquals(savedPost, service.getPost(1));

        service.savePost(updatedPost);
        service.dropTables();

        assertThrows(DataAccessException.class, () -> service.getPost(1));
    }

    @Test
    public void removePostShouldInvalidateGetPostAndCountTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel savedPost = new PostModel(1, "Hello Friends", 1);

        service.savePost(post);
        assertEquals(savedPost, service.getPost(1));
        assertEquals(1, service.getPostsCount());
        assertEquals(1, service.getPostsCountForCreator(1));

        service.removePost(1);
        service.dropTables();

        assertThrows(DataAccessException.class, () -> service.getPost(1));
        assertThrows(DataAccessException.class, service::getPostsCount);
        assertThrows(DataAccessException.class, () -> service.getPostsCountForCreator(1));
    }

    @Test
    public void cacheKickOutTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasya");
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);

        service.saveUser(user);
        service.savePost(post);
        assertEquals(expectedUser, service.getUser(1));
        assertEquals(expectedPost, service.getPost(1));
        assertEquals(1, service.getUsersCount());
        assertEquals(1, service.getPostsCount());

        service.dropTables();

        assertEquals(expectedPost, service.getPost(1));
        assertEquals(1, service.getUsersCount());
        assertEquals(1, service.getPostsCount());
        assertThrows(DataAccessException.class, () -> service.getUser(1));
    }
}
