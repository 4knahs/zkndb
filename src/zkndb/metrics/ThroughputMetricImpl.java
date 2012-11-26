/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.metrics;

/**
 *
 * @author 4knahs
 */
public class ThroughputMetricImpl implements Metric{

    private long _requests;
    private long _acks;
    
    public void init(){
        reset();
    }

    public long getRequests() {
        return _requests;
    }

    public void incrementRequests() {
        _requests++;
    }

    public long getAcks() {
        return _acks;
    }

    public void incrementAcks() {
        _acks++;
    }

    public void reset() {
        _requests = 0;
        _acks = 0;
    }
}

