/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.storage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;

/**
 *
 * @author 4knahs
 */
public class DummyStorageImpl extends Storage {

    public DummyStorageImpl(int id, int requestRate, List<Metric> shared) {
        _id = id;
        _sharedData = shared;
        _requestRate = requestRate;
    }

    @Override
    public void init() {
        //TODO : establishes connection to storage
        System.out.println("Storage " + _id + " establishing connection");
    }

    @Override
    public void write() {
        //TODO : performs a random write to storage
        Metric metric = _sharedData.get(_id);
        synchronized (metric) {
            ((ThroughputMetricImpl) metric).incrementRequests();
            try {
                //Do write to datastore
                //System.out.println("Storage " + _id + " random write.");
                ((ThroughputMetricImpl) metric).incrementAcks();
            } catch (Exception e) {
                //Sent request but it could not be served.
                //Should catch only specific exception.
            }
        }
    }

    @Override
    public void read() {
        //TODO : performs a read to storage
        
        synchronized (_sharedData.get(_id)) {
            ((ThroughputMetricImpl) _sharedData.get(_id)).incrementRequests();
            try {
                //Do read to datastore
                //System.out.println("Storage " + _id + " random read.");
                ((ThroughputMetricImpl) _sharedData.get(_id)).incrementAcks();
            } catch (Exception e) {
                //Sent request but it could not be served.
                //Should catch only specific exception.
            }
        }
    }

    @Override
    public void run() {
        while (_running) {
            write();
            try {
                Thread.sleep(_requestRate);
            } catch (InterruptedException ex) {
                Logger.getLogger(DummyStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
