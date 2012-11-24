/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.benchmark;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputEngineImpl;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.storage.DummyStorageImpl;
import zkndb.storage.Storage;

/**
 *
 * @author 4knahs
 */
public class DummyBenchmarkImpl extends Benchmark {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Expected arguments: nThreads metricPeriod executionTime requestRate");
            return;
        }

        _storageThreads = new ArrayList<Thread>();
        _sharedData = new ArrayList<Metric>();
        _storages = new ArrayList<Storage>();

        int nStorageThreads = new Integer(args[0]);
        long metricPeriod = new Long(args[1]);
        long executionTime = new Long(args[2]);
        int requestRate = new Integer(args[3]);

        //Allocate shared data
        for (int i = 0; i < nStorageThreads; i++) {
            _sharedData.add(new ThroughputMetricImpl());
        }

        //Create metrics
        _metrics = new ThroughputEngineImpl(_sharedData, metricPeriod);

        //Create storages
        for (int i = 0; i < nStorageThreads; i++) {
            _storages.add(new DummyStorageImpl(i,requestRate, _sharedData));
        }

        //Run storages
        for (Storage storage : _storages) {
            Thread storeThread = new Thread(((DummyStorageImpl) storage));
            _storageThreads.add(storeThread);
            storeThread.start();
        }

        //Run metrics
        _metricsThread = new Thread(_metrics);
        _metricsThread.start();
        
        try {
            //Calculate execution time
            Thread.sleep(executionTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(DummyBenchmarkImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Stop threads
        _metrics.stop();
        for (Storage storage : _storages) {
            ((DummyStorageImpl) storage).stop();
        }
    }
}
