package education.bert.benchmark;

import education.bert.PostgresConfig;
import education.bert.model.PostModel;
import education.bert.service.CachedForumService;
import education.bert.service.ForumService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ForumServiceBenchmark {

    @State(Scope.Benchmark)
    public static class ServiceState {
        public ForumService service = new ForumService();

        @Setup(Level.Trial)
        public void init() {
            service.setDbUrl(PostgresConfig.url);
        }

        @Setup(Level.Iteration)
        public void setup() {
            service.setup();
            LoadTestScenarios.addSomeInitialData(service);
//            Actions.doSomeInitialQueries(service);
        }
    }

    @State(Scope.Benchmark)
    public static class CachedServiceState {
        public CachedForumService service = new CachedForumService();

        @Setup(Level.Trial)
        public void init() {
            service.setDbUrl(PostgresConfig.url);
        }

        @Setup(Level.Iteration)
        public void setup() {
            service.setup(10);
            LoadTestScenarios.addSomeInitialData(service);
//            Actions.doSomeInitialQueries(service);
        }
    }

//    @Benchmark
//    public void addSomeNewPost(ServiceState state) {
//        Actions.addSomeNewPost(state.service);
//    }
//
//    @Benchmark
//    public void addSomeNewPostCachedService(CachedServiceState state) {
//        Actions.addSomeNewPost(state.service);
//    }

    @Benchmark
    public void getSomePost(ServiceState state, Blackhole blackhole) {
        PostModel post = LoadTestScenarios.getSomePost(state.service);
        blackhole.consume(post);
    }

    @Benchmark
    public void getSomePostCachedService(CachedServiceState state, Blackhole blackhole) {
        PostModel post = LoadTestScenarios.getSomePost(state.service);
        blackhole.consume(post);
    }
}
