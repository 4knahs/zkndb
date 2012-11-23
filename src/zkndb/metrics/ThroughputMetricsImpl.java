package zkndb.metrics;

import java.util.List;

/**
 *
 * @author 4knahs
 */
public class ThroughputMetricsImpl extends MetricsEngine{
    
    public ThroughputMetricsImpl(List<Metric> shared){
        _sharedData = shared;
    }

    @Override
    public void init(List<Metric> shared) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
