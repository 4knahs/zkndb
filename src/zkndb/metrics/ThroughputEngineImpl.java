package zkndb.metrics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.benchmark.BenchmarkUtils;

/**
 *
 * @author 4knahs
 */
public class ThroughputEngineImpl extends MetricsEngine {

    long previousRequests = 0;
    long previousAcks = 0;
    FileWriter fstream = null;
    BufferedWriter out = null;

    @Override
    public void init() {

        _sharedData = BenchmarkUtils.sharedData;
        _period = BenchmarkUtils.metricPeriod;

        for (Metric m : _sharedData) {
            synchronized (m) { //each storage thread should lock its own object
                m.reset();
            }
        }

        try {
            // Create file 
            fstream = new FileWriter(BenchmarkUtils.getFilename(),false);
            out = new BufferedWriter(fstream);
            
            out.write("#Number of storage threads,#" + BenchmarkUtils.nStorageThreads
                + "\n #Logging period,#" + BenchmarkUtils.metricPeriod
                + "\n #Total execution time (ms),#" + BenchmarkUtils.executionTime
                + "\n #Cycle time (ms),#" + BenchmarkUtils.requestRate
                + "\n #Sequential writes per cycle,#" + BenchmarkUtils.nWrites
                + "\n #Sequential reads per cycle,#" + BenchmarkUtils.nReads + "\n");
            
            out.write("Requests/s,Acks/s\n");

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        long totalRequests = 0;
        long totalAcks = 0;

        //Log requests/acks per second and reset
        for (Metric m : _sharedData) {
            //synchronized (m) { //each storage thread should lock its own object
//                System.out.println("Throughput = "
//                        + ((ThroughputMetricImpl) m).getRequests() + " Acks = "
//                        + ((ThroughputMetricImpl) m).getAcks());

            totalAcks += ((ThroughputMetricImpl) m).getAcks();
            totalRequests += ((ThroughputMetricImpl) m).getRequests();
            //m.reset();
            //}
        }
        try {
            //printTime();
            out.write("" + (totalRequests - previousRequests)
                    + "," + (totalAcks - previousAcks) + "\n");
        } catch (IOException ex) {
            Logger.getLogger(ThroughputEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("TotalRequests = " + (totalRequests - previousRequests)
        //        + " TotalAcks = " + (totalAcks - previousAcks));

        previousAcks = totalAcks;
        previousRequests = totalRequests;
    }

    @Override
    public void run() {
        while (_running) {
            update();
            try {
                Thread.sleep(BenchmarkUtils.metricPeriod);




            } catch (InterruptedException ex) {
                Logger.getLogger(ThroughputEngineImpl.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ThroughputEngineImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
