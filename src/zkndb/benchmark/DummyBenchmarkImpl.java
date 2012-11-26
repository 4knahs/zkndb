/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.benchmark;

import zkndb.exceptions.InvalidInputException;
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
    public static void main(String[] args){
        
        BenchmarkUtils.readInput(args);

        BenchmarkUtils.setMetric("ThroughputMetricImpl");
    
        BenchmarkUtils.setEngine("ThroughputEngineImpl");

        BenchmarkUtils.setStorage("DummyStorageImpl");
        
        BenchmarkUtils.run();
    }
}
