package zkndb.benchmark;

import zkndb.metrics.ThroughputMetricsImpl;
import zkndb.storage.ZKStorageImpl;

/**
 *
 * @author 4knahs
 */
public class ZKBenchmark extends Benchmark{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        _storage = new ZKStorageImpl(_sharedData);
        _metrics = new ThroughputMetricsImpl(_sharedData);
        
        
        Thread storeThread = new Thread(_storage);
        Thread metricsThread = new Thread(_metrics);
        
        storeThread.start();
        metricsThread.start();
    }
}
