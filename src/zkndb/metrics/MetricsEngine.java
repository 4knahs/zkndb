package zkndb.metrics;

import java.util.List;

/**
 *
 * @author 4knahs
 */

//Thread that runs from time to time and logs the metrics
public abstract class MetricsEngine implements Runnable{
    List<Metric> _sharedData;

    //initializes logs and metric variables
    public abstract void init(List<Metric> shared);
    
    //logs the current metrics
    public abstract void update();
}
