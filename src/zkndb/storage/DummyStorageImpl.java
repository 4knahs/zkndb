/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.storage;

import zkndb.benchmark.BenchmarkUtils;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;

/**
 *
 * @author 4knahs
 */
public class DummyStorageImpl extends Storage {

    public DummyStorageImpl() {}

    @Override
    public void init() {
        _sharedData = BenchmarkUtils.sharedData;
        _requestRate = BenchmarkUtils.requestRate;
        //TODO : establishes connection to storage
        System.out.println("Storage " + _id + " establishing connection");
    }

    @Override
    public void write() {
        //TODO : performs a random write to storage
        Metric metric = _sharedData.get(_id);
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

    @Override
    public void read() {
        //TODO : performs a read to storage

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
