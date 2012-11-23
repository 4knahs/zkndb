package zkndb.benchmark;

import zkndb.metrics.ThroughputMetricsImpl;
import zkndb.storage.NdbStorageImpl;

/**
 *
 * @author 4knahs
 */
public class NdbBenchmark extends Benchmark{
     /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        _storage = new NdbStorageImpl();
        _metrics = new ThroughputMetricsImpl();
        
        _storage.init();
        _metrics.init();
        
        Thread storeThread = new Thread(_storage);
        Thread metricsThread = new Thread(_metrics);
        
        storeThread.start();
        metricsThread.start();
    }
}
