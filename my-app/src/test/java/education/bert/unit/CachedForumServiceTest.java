package education.bert.unit;

import education.bert.PostgresConfig;
import education.bert.benchmark.AForumServiceBenchmark;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedForumServiceTest extends AForumServiceTest {
    private final CachedForumService service = new CachedForumService();

    {
        service.setDbUrl(PostgresConfig.url);
        super.setService(service);
    }

    @AfterAll
    static void benchmarkRunner() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(AForumServiceBenchmark.class.getSimpleName())
                .forks(1)
//                .warmupIterations(10)
//                .measurementIterations(10)
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
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getUser(1)", expectedUser);
        List<String> expectedKeys = Arrays.asList("getUser(1)");

        service.saveUser(user);
        UserModel firstGetUser = service.getUser(1);
        assertEquals(expectedUser, firstGetUser);
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        UserModel secondGetUser = service.getUser(1);
        assertEquals(expectedUser, secondGetUser);
    }

    @Test
    public void getPostFromCacheTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPost(1)", expectedPost);
        List<String> expectedKeys = Arrays.asList("getPost(1)");

        service.savePost(post);
        PostModel firstGetPost = service.getPost(1);
        assertEquals(expectedPost, firstGetPost);
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        PostModel secondGetPost = service.getPost(1);
        assertEquals(expectedPost, secondGetPost);
    }

    @Test
    public void getPostsCountForCreatorFromCacheTest() {
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPostsCountForCreator(1)", 2);
        List<String> expectedKeys = Arrays.asList("getPostsCountForCreator(1)");

        super.getPostsCountForCreatorTest();
        assertEquals(2, service.getPostsCountForCreator(1));
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());
    }

    @Test
    public void saveUserShouldInvalidateGetUsersCountTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel newUser = new UserModel(0, "Petya");
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getUsersCount()", 1);
        List<String> expectedKeys = Arrays.asList("getUsersCount()");

        service.saveUser(user);
        assertEquals(1, service.getUsersCount());
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.saveUser(newUser);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void updateUserShouldInvalidateGetUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel savedUser = new UserModel(1, "Vasya");
        UserModel updatedUser = new UserModel(1, "Vasiliy");
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getUser(1)", savedUser);
        List<String> expectedKeys = Arrays.asList("getUser(1)");

        service.saveUser(user);
        assertEquals(savedUser, service.getUser(1));
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.saveUser(updatedUser);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void removeUserShouldInvalidateGetUserAndCountTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel savedUser = new UserModel(1, "Vasya");
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getUser(1)", savedUser);
        expectedMap.put("getUsersCount()", 1);
        List<String> expectedKeys = Arrays.asList("getUser(1)", "getUsersCount()");

        service.saveUser(user);
        assertEquals(savedUser, service.getUser(1));
        assertEquals(1, service.getUsersCount());
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.removeUser(1);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void savePostShouldInvalidateGetPostsCountTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel newPost = new PostModel(0, "Forum Rules", 2);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPostsCount()", 1);
        expectedMap.put("getPostsCountForCreator(2)", 0);
        List<String> expectedKeys = Arrays.asList("getPostsCount()", "getPostsCountForCreator(2)");

        service.savePost(post);
        assertEquals(1, service.getPostsCount());
        assertEquals(0, service.getPostsCountForCreator(2));
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.savePost(newPost);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void updatePostShouldInvalidateGetPostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel savedPost = new PostModel(1, "Hello Friends", 1);
        PostModel updatedPost = new PostModel(1, "Hello Folks", 1);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPost(1)", savedPost);
        List<String> expectedKeys = Arrays.asList("getPost(1)");

        service.savePost(post);
        assertEquals(savedPost, service.getPost(1));
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.savePost(updatedPost);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void removePostShouldInvalidateGetPostAndCountTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel savedPost = new PostModel(1, "Hello Friends", 1);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPost(1)", savedPost);
        expectedMap.put("getPostsCount()", 1);
        expectedMap.put("getPostsCountForCreator(1)", 1);
        List<String> expectedKeys = Arrays.asList("getPost(1)", "getPostsCount()", "getPostsCountForCreator(1)");

        service.savePost(post);
        assertEquals(savedPost, service.getPost(1));
        assertEquals(1, service.getPostsCount());
        assertEquals(1, service.getPostsCountForCreator(1));
        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());

        service.removePost(1);
        assertTrue(service.getCacheMap().isEmpty());
        assertTrue(service.getCacheKeys().isEmpty());
    }

    @Test
    public void cacheKickOutTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasya");
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("getPost(1)", expectedPost);
        expectedMap.put("getUsersCount()", 1);
        expectedMap.put("getPostsCount()", 1);
        List<String> expectedKeys = Arrays.asList("getPost(1)", "getUsersCount()", "getPostsCount()");

        service.saveUser(user);
        service.savePost(post);
        assertEquals(expectedUser, service.getUser(1));
        assertEquals(expectedPost, service.getPost(1));
        assertEquals(1, service.getUsersCount());
        assertEquals(1, service.getPostsCount());

        assertEquals(expectedMap, service.getCacheMap());
        assertEquals(expectedKeys, service.getCacheKeys());
    }
}
