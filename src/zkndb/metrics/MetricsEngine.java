package zkndb.metrics;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    protected SimpleDateFormat ftcomplete = 
       new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a");
    protected SimpleDateFormat ft = 
       new SimpleDateFormat ("hh_mm_ss");

    //initializes logs and metric variables
    public abstract void init();
    
    //logs the current metrics
    public abstract void update();
    
    public void stop(){
        _running = false;
    }
    
    public void printCompleteTime(){
        Date dnow = new Date();
        System.out.print(ftcomplete.format(dnow));
    }
    
    public void printTime(){
        Date dnow = new Date();
        System.out.print(ft.format(dnow));
    }
    
    public String getTime(){
        Date dnow = new Date();
        return ft.format(dnow);
    }
}
