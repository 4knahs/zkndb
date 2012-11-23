package zkndb.storage;

import java.util.List;
import zkndb.metrics.Metric;

public class NdbStorageImpl extends StorageInterface{
    List<Metric> _sharedData;
    
    public NdbStorageImpl(List<Metric> shared){
        _sharedData = shared;
    }
    
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
    }
    
}
