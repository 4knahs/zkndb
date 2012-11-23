/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.storage;

/**
 *
 * @author 4knahs
 */
public class DummyStorageImpl extends Storage{

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read() {
    }

    @Override
    public void run() {
        while(_running){
            
        }
    }
    
}
