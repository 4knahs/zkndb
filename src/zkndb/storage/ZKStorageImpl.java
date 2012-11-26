package zkndb.storage;

import java.util.List;
import zkndb.benchmark.BenchmarkUtils;
import zkndb.metrics.Metric;

/**
 *
 * @author 4knahs
 */
public class ZKStorageImpl extends Storage{

    @Override
    public void write() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init() {
        _sharedData = BenchmarkUtils.sharedData;
    }
    
}
