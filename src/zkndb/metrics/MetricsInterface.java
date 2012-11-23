package zkndb.metrics;

/**
 *
 * @author 4knahs
 */
public interface MetricsInterface extends Runnable{
    
    //initializes logs and metric variables
    void init();
    
    //logs the current metrics
    void update();
}
