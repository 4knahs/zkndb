package zkndb.metrics;

/**
 *
 * @author 4knahs
 */

//Thread that runs from time to time and logs the metrics
public interface MetricsInterface extends Runnable{
    
    //initializes logs and metric variables
    void init();
    
    //logs the current metrics
    void update();
}
