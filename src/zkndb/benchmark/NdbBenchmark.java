package zkndb.benchmark;

import java.util.ArrayList;
import zkndb.metrics.Metric;
import zkndb.metrics.Throughput;
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
        
        _sharedData = new ArrayList<Metric>();
        
        _storage = new NdbStorageImpl(_sharedData);
        _metrics = new ThroughputMetricsImpl(_sharedData);
        
        Thread storeThread = new Thread(_storage);
        Thread metricsThread = new Thread(_metrics);
        
        storeThread.start();
        metricsThread.start();
    }
}
