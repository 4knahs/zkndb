package zkndb.metrics;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 4knahs
 */
public class ThroughputEngineImpl extends MetricsEngine {

    public ThroughputEngineImpl(List<Metric> shared) {
        _sharedData = shared;
    }

    @Override
    public void init(List<Metric> shared) {

        for (Metric m : _sharedData) {
            synchronized (m) { //each storage thread should lock its own object
                ((ThroughputMetricImpl) m).reset();
            }
        }
    }

    @Override
    public void update() {
        long totalRequests = 0;
        long totalAcks = 0;

        //Log requests/acks per second and reset
        for (Metric m : _sharedData) {
            synchronized (m) { //each storage thread should lock its own object
                System.out.println("Throughput = "
                        + ((ThroughputMetricImpl) m).getRequests() + " Acks = "
                        + ((ThroughputMetricImpl) m).getAcks());
                ((ThroughputMetricImpl) m).reset();

                totalRequests += ((ThroughputMetricImpl) m).getRequests();
                totalAcks += ((ThroughputMetricImpl) m).getAcks();
            }
        }
        System.out.println("TotalRequests = " + totalRequests + " TotalAcks = " + totalAcks);
    }

    @Override
    public void run() {
        while(_running){
            update();
        }
    }
}
