package education.bert.unit;

import education.bert.model.UserModel;
import education.bert.repository.PostgresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PostgresRepositoryTest {
    private final PostgresRepository repository = new PostgresRepository();
    //    private final String url = "jdbc:postgresql://localhost:5432/travis_ci_test?user=postgres";
    private final String url = "jdbc:postgresql://192.168.99.100:32768/postgres?user=postgres";

    {
        repository.setUrl(url);
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
        assertEquals(expectedUser, repository.getUser(1).get());
    }

    @Test
    public void updateUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        UserModel expectedUser = new UserModel(1, "Vasiliy");

        UserModel updatedUser = repository.saveUser(user);
        updatedUser.setName("Vasiliy");

        assertEquals(updatedUser, repository.saveUser(updatedUser));
        assertEquals(expectedUser, repository.getUser(updatedUser.getId()).get());
    }

    @Test
    public void removeUserTest() {
        UserModel user = new UserModel(0, "Vasya");
        repository.saveUser(user);
        repository.removeUser(1);
        assertFalse(repository.getUser(1).isPresent());
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
}
