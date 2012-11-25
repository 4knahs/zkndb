package zkndb.storage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.benchmark.BenchmarkUtils;
import zkndb.metrics.Metric;

/**
 *
 * @author 4knahs
 */
public abstract class Storage implements Runnable{
    protected List<Metric> _sharedData;
    protected int _id;
    protected Boolean _running = true;
    protected int _requestRate = 100;
    
    
    //establishes connection to storage
    public abstract void init();
    
    //performs a random write to storage
    public abstract void write();
    
    //performs a read to storage
    public abstract void read();
    
    public void stop(){
        _running = false;
    }
    
    @Override
    public void run() {
        while (_running) {
            for(int i=0; i < BenchmarkUtils.nWrites; i++){
                write();
            }
            for(int i=0; i < BenchmarkUtils.nReads; i++){
                read();
            }
            try {
                Thread.sleep(_requestRate);
            } catch (InterruptedException ex) {
                Logger.getLogger(DummyStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
