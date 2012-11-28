/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.benchmark;

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
