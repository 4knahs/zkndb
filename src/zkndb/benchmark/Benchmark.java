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
    protected static StorageInterface _storage;
    public static ArrayList<Metric> _sharedData;    
    
}
