package zkndb.benchmark;

import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.exceptions.InvalidInputException;
import zkndb.metrics.ThroughputEngineImpl;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.storage.Storage;
import zkndb.storage.ZKStorageImpl;

/**
 *
 * @author 4knahs
 */
public class ZKBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        BenchmarkUtils.readInput(args);

        BenchmarkUtils.setMetric("ThroughputMetricImpl");
    
        BenchmarkUtils.setEngine("ThroughputEngineImpl");

        BenchmarkUtils.setStorage("ZkStorageImpl");
        
        BenchmarkUtils.run();
    }
}
