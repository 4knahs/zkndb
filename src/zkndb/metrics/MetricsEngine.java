package zkndb.metrics;

import java.util.List;

/**
 *
 * @author 4knahs
 */

//Thread that runs from time to time and logs the metrics
public abstract class MetricsEngine implements Runnable{
    protected List<Metric> _sharedData;
    protected Boolean _running = true;
    protected long _period = 1000;

    //initializes logs and metric variables
    public abstract void init(List<Metric> shared);
    
    //logs the current metrics
    public abstract void update();
    
    public void stop(){
        _running = false;
    }
}
