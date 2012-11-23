package zkndb.storage;

import java.util.List;
import zkndb.metrics.Metric;

/**
 *
 * @author 4knahs
 */
public abstract class Storage implements Runnable{
    List<Metric> _sharedData;
    public Boolean _running = true;
    
    //establishes contact to storage
    public abstract void init();
    
    //performs a random write to storage
    public abstract void write();
    
    //performs a read to storage
    public abstract void read();
    
    public void stop(){
        _running = false;
    }
    
}
