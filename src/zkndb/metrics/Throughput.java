/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.metrics;

/**
 *
 * @author 4knahs
 */
public class Throughput implements Metric{

    private int _requests;
    private int _acks;

    public Throughput() {
        reset();
    }

    public int getRequests() {
        return _requests;
    }

    public void incrementRequests() {
        _requests++;
    }

    public int getAcks() {
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

