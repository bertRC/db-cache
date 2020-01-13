package education.bert.unit;

import education.bert.PostgresConfig;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.repository.PostgresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PostgresRepositoryTest {
    private final PostgresRepository repository = new PostgresRepository();

    {
        repository.setUrl(PostgresConfig.url);
    }

    @BeforeEach
    public void setup() {
        repository.setup();
    }

    @Test
    public void saveGetUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasya");
        assertEquals(expectedUser, repository.saveUser(user));
        assertEquals(expectedUser, repository.getUser(1).orElse(null));
    }

    @Test
    public void saveGetPostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Friends", 1);
        assertEquals(expectedPost, repository.savePost(post));
        assertEquals(expectedPost, repository.getPost(1).orElse(null));
    }

    @Test
    public void updateUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasiliy");

        UserModel updatedUser = repository.saveUser(user);
        updatedUser.setName("Vasiliy");

        assertEquals(updatedUser, repository.saveUser(updatedUser));
        assertEquals(expectedUser, repository.getUser(updatedUser.getId()).orElse(null));
    }

    @Test
    public void updatePostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        PostModel expectedPost = new PostModel(1, "Hello Folks", 1);

        PostModel updatedPost = repository.savePost(post);
        updatedPost.setPostName("Hello Folks");

        assertEquals(updatedPost, repository.savePost(updatedPost));
        assertEquals(expectedPost, repository.getPost(updatedPost.getId()).orElse(null));
    }

    @Test
    public void removeUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        repository.saveUser(user);
        repository.removeUser(1);
        assertFalse(repository.getUser(1).isPresent());
    }

    @Test
    public void removePostTest() {
        PostModel post = new PostModel(0, "Hello Friends", 1);
        repository.savePost(post);
        repository.removePost(1);
        assertFalse(repository.getPost(1).isPresent());
    }

    @Test
    public void getUsersCountTest() {
        assertEquals(0, repository.getUsersCount());
        repository.saveUser(new UserModel(0, "Vasya"));
        assertEquals(1, repository.getUsersCount());
        repository.saveUser(new UserModel(0, "Petya"));
        assertEquals(2, repository.getUsersCount());
        repository.saveUser(new UserModel(1, "Vasiliy"));
        assertEquals(2, repository.getUsersCount());
        repository.removeUser(1);
        assertEquals(1, repository.getUsersCount());
    }

    @Test
    public void getPostsCountTest() {
        assertEquals(0, repository.getPostsCount());
        repository.savePost(new PostModel(0, "Hello Friends", 1));
        assertEquals(1, repository.getPostsCount());
        repository.savePost(new PostModel(0, "Forum Rules", 2));
        assertEquals(2, repository.getPostsCount());
        repository.savePost(new PostModel(1, "Hello Folks", 1));
        assertEquals(2, repository.getPostsCount());
        repository.removePost(2);
        assertEquals(1, repository.getPostsCount());
    }

    @Test
    public void getPostsCountForCreator() {
        repository.savePost(new PostModel(0, "Hello Friends", 1));
        repository.savePost(new PostModel(0, "Forum Rules", 2));
        repository.savePost(new PostModel(0, "General Discussion", 1));
        assertEquals(2, repository.getPostsCountForCreator(1));
    }
}
