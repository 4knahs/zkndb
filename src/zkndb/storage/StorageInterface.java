package zkndb.storage;

import java.util.List;
import zkndb.metrics.Metric;

/**
 *
 * @author 4knahs
 */
public interface StorageInterface extends Runnable{
    
    //establishes contact to storage
    void init();
    
    //performs a random write to storage
    void write();
    
    //performs a read to storage
    void read();
    
}
