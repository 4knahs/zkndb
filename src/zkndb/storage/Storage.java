package zkndb.storage;

import java.util.List;
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
    
}
