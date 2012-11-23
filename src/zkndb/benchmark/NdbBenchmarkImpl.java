package zkndb.benchmark;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.metrics.ThroughputEngineImpl;
import zkndb.storage.NdbStorageImpl;
import zkndb.storage.Storage;

/**
 *
 * @author 4knahs
 */
public class NdbBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if(args.length != 3){
            System.out.println("Expected arguments: nThreads metricPeriod executionTime");
            return;
        }

        _storageThreads = new ArrayList<Thread>();
        _sharedData = new ArrayList<Metric>();
        _storages = new ArrayList<Storage>();
        
        int nStorageThreads = new Integer(args[0]);
        long metricPeriod = new Long(args[1]);
        long executionTime = new Long(args[2]);
        
        //Allocate shared data
        for(int i=0;i<nStorageThreads;i++){
            _sharedData.add(new ThroughputMetricImpl());
        }
        
        //Create metrics
        _metrics = new ThroughputEngineImpl(_sharedData);
        
        //Create storages
        for(int i=0;i<nStorageThreads;i++){
            _storages.add(new NdbStorageImpl(_sharedData));
        }
        
        //Run storages
        for (Storage storage : _storages) {
            Thread storeThread = new Thread(((NdbStorageImpl) storage));
            _storageThreads.add(storeThread);
            storeThread.start();
        }
        
        //Run metrics
        _metricsThread = new Thread(_metrics);
        _metricsThread.start();
        
        //Calculate execution time
        for(;executionTime > 0; executionTime -=  metricPeriod){
            try {
                Thread.sleep(metricPeriod);
            } catch (InterruptedException ex) {
                Logger.getLogger(NdbBenchmarkImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Stop threads
        _metrics.stop();
        for (Storage storage : _storages) {
            ((NdbStorageImpl) storage).stop();
        }
    }
}
