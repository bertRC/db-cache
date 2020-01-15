package education.bert.benchmark;

import education.bert.PostgresConfig;
import education.bert.service.CachedForumService;
import education.bert.service.ForumService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class ForumServiceBenchmark {
    private ForumService service = new ForumService();
    private CachedForumService cachedService = new CachedForumService();

    @Setup(Level.Trial)
    public void init() {
        service.setDbUrl(PostgresConfig.url);
        cachedService.setDbUrl(PostgresConfig.url);
    }

    @Setup(Level.Iteration)
    public void setup() {
        cachedService.setup(10);
        LoadTestScenarios.addSomeInitialData(service);
    }

    @Benchmark
    public void getSomePost(Blackhole blackhole) {
        blackhole.consume(LoadTestScenarios.getSomePost(service));
    }

    @Benchmark
    public void getSomePostCachedService(Blackhole blackhole) {
        blackhole.consume(LoadTestScenarios.getSomePost(cachedService));
    }
}
