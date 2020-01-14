package education.bert.unit;

import education.bert.PostgresConfig;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.service.ForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ForumServiceTest {
    private ForumService service = new ForumService();

    {
        service.setDbUrl(PostgresConfig.url);
    }

    public void setService(ForumService service) {
        this.service = service;
    }

    @BeforeEach
    public void setup() {
        service.setup();
    }

    @Test
    public void saveGetUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasya");
        assertEquals(expectedUser, service.saveUser(user));
        assertEquals(expectedUser, service.getUser(1));
    }

    @Test
    public void saveGetPostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);
        assertEquals(expectedPost, service.savePost(post));
        assertEquals(expectedPost, service.getPost(1));
    }

    @Test
    public void updateUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasiliy");

        UserModel updatedUser = service.saveUser(user);
        updatedUser.setName("Vasiliy");

        assertEquals(updatedUser, service.saveUser(updatedUser));
        assertEquals(expectedUser, service.getUser(updatedUser.getId()));
    }

    @Test
    public void updatePostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Folks", 1);

        PostModel updatedPost = service.savePost(post);
        updatedPost.setPostName("Hello Folks");

        assertEquals(updatedPost, service.savePost(updatedPost));
        assertEquals(expectedPost, service.getPost(updatedPost.getId()));
    }

    @Test
    public void updateNotExistedUserTest() {
        UserModel user = new UserModel(1, "Vasiliy");
        UserModel result = service.saveUser(user);
        assertNull(result);
        assertEquals(0, service.getUsersCount());
    }

    @Test
    public void updateNotExistedPostTest() {
        PostModel post = new PostModel(1, "Hello Friends", 1);
        PostModel result = service.savePost(post);
        assertNull(result);
        assertEquals(0, service.getPostsCount());
    }

    @Test
    public void removeUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        service.saveUser(user);
        assertTrue(service.removeUser(1));
        assertNull(service.getUser(1));
    }

    @Test
    public void removePostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        service.savePost(post);
        assertTrue(service.removePost(1));
        assertNull(service.getPost(1));
    }

    @Test
    public void removeNotExistedUserTest() {
        assertNull(service.getUser(1));
        assertFalse(service.removeUser(1));
    }

    @Test
    public void removeNotExistedPostTest() {
        assertNull(service.getPost(1));
        assertFalse(service.removePost(1));
    }

    @Test
    public void getUsersCountTest() {
        assertEquals(0, service.getUsersCount());
        service.saveUser(new UserModel(0, "Vasya"));
        assertEquals(1, service.getUsersCount());
        service.saveUser(new UserModel(0, "Petya"));
        assertEquals(2, service.getUsersCount());
        service.saveUser(new UserModel(1, "Vasiliy"));
        assertEquals(2, service.getUsersCount());
        service.removeUser(1);
        assertEquals(1, service.getUsersCount());
    }

    @Test
    public void getPostsCountTest() {
        assertEquals(0, service.getPostsCount());
        service.savePost(new PostModel(0, "Hello Friends", 1));
        assertEquals(1, service.getPostsCount());
        service.savePost(new PostModel(0, "Forum Rules", 2));
        assertEquals(2, service.getPostsCount());
        service.savePost(new PostModel(1, "Hello Folks", 1));
        assertEquals(2, service.getPostsCount());
        service.removePost(2);
        assertEquals(1, service.getPostsCount());
    }

    @Test
    public void getPostsCountForCreatorTest() {
        service.savePost(new PostModel(0, "Hello Friends", 1));
        service.savePost(new PostModel(0, "Forum Rules", 2));
        service.savePost(new PostModel(0, "General Discussion", 1));
        assertEquals(2, service.getPostsCountForCreator(1));
    }
}
