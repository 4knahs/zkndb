package zkndb.benchmark;

/**
 *
 * @author 4knahs
 */
public class NdbBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        BenchmarkUtils.readInput(args);

        BenchmarkUtils.setMetric("ThroughputMetricImpl");
    
        BenchmarkUtils.setEngine("ThroughputEngineImpl");

        BenchmarkUtils.setStorage("NdbStorageImpl");
        
        BenchmarkUtils.run();
    }
}
