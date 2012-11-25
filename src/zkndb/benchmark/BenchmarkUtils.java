/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.benchmark;

import zkndb.exceptions.InvalidInputException;
import java.util.ArrayList;
import zkndb.metrics.Metric;
import zkndb.metrics.MetricsEngine;
import zkndb.storage.Storage;

/**
 *
 * @author 4knahs
 */
public abstract class BenchmarkUtils {

    public static int nStorageThreads = 1;
    public static long metricPeriod = 1000;
    public static long executionTime = 10000;
    public static int requestRate = 100;
    public static int nWrites = 1;
    public static int nReads = 1;
    
    public static ArrayList<Metric> sharedData = new ArrayList<Metric>();
    
    protected static MetricsEngine metrics;
    protected static ArrayList<Storage> storages = new ArrayList<Storage>(); 
    
    protected static ArrayList<Thread> storageThreads = new ArrayList<Thread>();
    protected static Thread metricsThread;

    public static void setMetricsEngine(MetricsEngine engine){
        metrics = engine;
    }
    
    public static void startMetrics(){
        metricsThread = new Thread(BenchmarkUtils.metrics);
        BenchmarkUtils.metricsThread.start();
    }
    
    
    
    public static void init(String[] args) throws InvalidInputException{
        if (args.length == 4) {
            nStorageThreads = new Integer(args[0]);
            metricPeriod = new Long(args[1]);
            executionTime = new Long(args[2]);
            requestRate = new Integer(args[3]);
        } else {
            if (args.length == 6) {
                nStorageThreads = new Integer(args[0]);
                metricPeriod = new Long(args[1]);
                executionTime = new Long(args[2]);
                requestRate = new Integer(args[3]);
                nWrites = new Integer(args[4]);
                nReads = new Integer(args[5]);
            } else {
                System.out.println("Expected arguments: nThreads metricPeriod executionTime requestRate nWrites nReads");
                return;
            }
        }
        
        System.out.println("Running:\n Number of storage threads = " + nStorageThreads +
                        "\n Logging period = " + metricPeriod +
                        "\n Total execution time (ms) = " + executionTime +
                        "\n Cycle time (ms) = " + requestRate +
                        "\n Sequential writes per cycle = " + nWrites +
                        "\n Sequential reads per cycle = " + nReads);
    }
    
}
