package education.bert.unit;

import education.bert.PostgresConfig;
import education.bert.service.CachedForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CachedForumServiceTest extends ForumServiceTest {
    private final CachedForumService service = new CachedForumService();

    {
        service.setDbUrl(PostgresConfig.url);
        super.setService(service);
    }

    @BeforeEach
    @Override
    public void setup() {
        service.setup(3);
    }


}
