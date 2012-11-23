package zkndb.benchmark;

import java.util.ArrayList;
import zkndb.metrics.Metric;
import zkndb.metrics.MetricsEngine;
import zkndb.storage.StorageInterface;

/**
 *
 * @author 4knahs
 */
public abstract class Benchmark {
    protected static MetricsEngine _metrics;
    protected static ArrayList<StorageInterface> _storages;
    protected static ArrayList<Metric> _sharedData; 
    
    protected static ArrayList<Thread> _storageThreads;
    protected static Thread _metricsThread;
}
