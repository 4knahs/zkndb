package zkndb.benchmark;

import zkndb.metrics.MetricsInterface;
import zkndb.storage.StorageInterface;

/**
 *
 * @author 4knahs
 */
public abstract class Benchmark {
    protected static MetricsInterface _metrics;
    protected static StorageInterface _storage;
}
