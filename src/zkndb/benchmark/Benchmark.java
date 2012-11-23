package zkndb.benchmark;

import zkndb.metrics.MetricsInterface;
import zkndb.storage.StorageInterface;

/**
 *
 * @author 4knahs
 */
public abstract class Benchmark {
    protected MetricsInterface _metrics;
    protected StorageInterface _storage;
}
