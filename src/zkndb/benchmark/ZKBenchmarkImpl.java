package zkndb.benchmark;

import java.util.ArrayList;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.metrics.ThroughputEngineImpl;
import zkndb.storage.NdbStorageImpl;
import zkndb.storage.Storage;
import zkndb.storage.ZKStorageImpl;

/**
 *
 * @author 4knahs
 */
public class ZKBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        _storageThreads = new ArrayList<Thread>();
        _sharedData = new ArrayList<Metric>();
        _storages = new ArrayList<Storage>();
        
        int nStorageThreads = 3;
        
        //Allocate shared data
        for(int i=0;i<nStorageThreads;i++){
            _sharedData.add(new ThroughputMetricImpl());
        }
        
        //Create metrics
        _metrics = new ThroughputEngineImpl(_sharedData);
        
        //Create storages
        for(int i=0;i<nStorageThreads;i++){
            _storages.add(new ZKStorageImpl(_sharedData));
        }
        
        //Run storages
        for (Storage storage : _storages) {
            Thread storeThread = new Thread(((ZKStorageImpl) storage));
            _storageThreads.add(storeThread);
            storeThread.start();
        }
        
        //Run metrics
        _metricsThread = new Thread(_metrics);
        _metricsThread.start();
    }
}
