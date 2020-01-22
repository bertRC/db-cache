package education.bert.benchmark;

import education.bert.PostgresConfig;
import education.bert.service.CachedForumService;
import education.bert.service.ForumService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark class for testing both service without cache and service with cache.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class ForumServiceBenchmark {

    /**
     * Service without cache to test.
     */
    private ForumService service = new ForumService();

    /**
     * Service with cache to test.
     */
    private CachedForumService cachedService = new CachedForumService();

    {
        service.setDbUrl(PostgresConfig.url);
        cachedService.setDbUrl(PostgresConfig.url);
    }

    /**
     * Main method to run benchmark tests. Using the code below, you can run benchmarks anywhere you like, in particular
     * inside unit tests.
     *
     * @param args standard psvm arguments.
     * @throws RunnerException if JMH Runner issues occur.
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ForumServiceBenchmark.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .build();
        new Runner(options).run();
    }

    /**
     * Setups the cache for cached service and adds initial data to DB. This method is invoked before each iteration.
     */
    @Setup(Level.Iteration)
    public void setup() {
        cachedService.setup(20);
        LoadTestScenarios.addSomeInitialData(service);
    }

    /**
     * Executes getPost query via uncached service.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void getSomePost(Blackhole blackhole) {
        blackhole.consume(LoadTestScenarios.getSomePost(service));
    }

    /**
     * Executes getPost query via cached service.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void getSomePostCachedService(Blackhole blackhole) {
        blackhole.consume(LoadTestScenarios.getSomePost(cachedService));
    }

    /**
     * Executes particular load test scenario via uncached service.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @Benchmark
    public void particularLoadTestScenario(Blackhole blackhole) {
        LoadTestScenarios.particularLoadTestScenario(service, blackhole);
    }

    /**
     * Executes particular load test scenario via cached service.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @Benchmark
    public void particularLoadTestScenarioCachedService(Blackhole blackhole) {
        LoadTestScenarios.particularLoadTestScenario(cachedService, blackhole);
    }

    /**
     * Executes random load test scenario via uncached service in 10 threads.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @Threads(100)
    @Benchmark
    public void randomLoadTestScenario(Blackhole blackhole) {
        LoadTestScenarios.randomLoadTestScenario(service, blackhole);
    }

    /**
     * Executes random load test scenario via cached service in 10 threads.
     *
     * @param blackhole JMH Blackhole object for consuming the queried data.
     */
    @Threads(100)
    @Benchmark
    public void randomLoadTestScenarioCachedService(Blackhole blackhole) {
        LoadTestScenarios.randomLoadTestScenario(cachedService, blackhole);
    }
}
