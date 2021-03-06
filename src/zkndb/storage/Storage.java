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
    protected long _appId = 0;
    
    //establishes connection to storage
    public abstract void init();
    
    //performs a random write to storage
    public abstract void write();
    
    //performs a read to storage
    public abstract void read();
    
    public void setId(int id){
        _id = id;
    }
    
    public void stop(){
        _running = false;
    }
    
    @Override
    public void run() {
        if(BenchmarkUtils.nWrites == 0)
        {
            //when we are performing read intensive workload
            //we write one value, and always read it throughout
            //the benchmark process
            write();
        }
        while (_running) {
            for(int i=0; i < BenchmarkUtils.nWrites; i++){
                write();
            }
            for(int i=0; i < BenchmarkUtils.nReads; i++){
                read();
            }
            try {
                Thread.sleep(BenchmarkUtils.requestRate);
            } catch (InterruptedException ex) {
                Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
