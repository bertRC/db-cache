package education.bert.benchmark;

import education.bert.PostgresConfig;
import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.service.ForumService;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class AForumServiceBenchmark {

    @State(Scope.Benchmark)
    public static class MyState {
        public ForumService service = new ForumService();
        public UserModel user = new UserModel(0, "Vasya");
        public PostModel post = new PostModel(0, "Hello Friends", 1);
        //TODO: добавить в бд несколько первоначальных записей перед тестами

        @Setup(Level.Trial)
        public void init() {
            service.setDbUrl(PostgresConfig.url);
        }

        @Setup(Level.Iteration)
        public void setup() {
            service.setup();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void addUserAndPost(MyState state) {
        state.service.saveUser(state.user);
        state.service.savePost(state.post);
    }
}
