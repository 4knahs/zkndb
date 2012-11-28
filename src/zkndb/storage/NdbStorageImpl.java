package zkndb.storage;

/**
 *
 * @author arinto
 */

import com.mysql.clusterj.ClusterJHelper;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.SessionFactory;
import com.mysql.clusterj.annotation.Lob;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.benchmark.BenchmarkUtils;
import zkndb.exceptions.InvalidDbEntryException;
import zkndb.metrics.ThroughputMetricImpl;

public class NdbStorageImpl extends Storage{
    
    private SessionFactory _factory;
    private Session _session;
    private int _randomByteSize;

    private byte[] _rndAppByte;
    private long _appId;
    
    @Override
    public void write() {
        ThroughputMetricImpl throughputMetric = null;

        throughputMetric = (ThroughputMetricImpl) _sharedData.get(_id);
        synchronized (throughputMetric) {
            try {

                throughputMetric.incrementRequests();
                storeApplicationState();
                throughputMetric.incrementAcks();
            } catch (Exception ex) {
                //assumptions: 
                //1. no exception is thrown during incrementing requests and acks
                //2. persists throws exception
                System.out.println("Exception in when trying to store application state");
                Logger.getLogger(NdbStorageImpl.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    @Override
    public void read() {
        ThroughputMetricImpl throughputMetric = null;
        throughputMetric = (ThroughputMetricImpl) _sharedData.get(_id);
        synchronized (throughputMetric) {
            try {
                throughputMetric.incrementRequests();
                readApplicationState();
                throughputMetric.incrementAcks();

            } catch (InvalidDbEntryException ex) {
                //theoretically this case will never happen
                //because we already ensure that new _appIds is stored when 
                //we sucessfully persist the data
                //therefore, to handle this case we treat this as succesfully increment
                throughputMetric.incrementAcks();
            } catch (Exception ex) {
                //assumptions: 
                //1. no exception is thrown during incrementing requests and acks
                //2. persists throws exception
                System.out.println("Exception in when trying to read application state");
                Logger.getLogger(NdbStorageImpl.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    @Override
    public void init() {
        
        _sharedData = BenchmarkUtils.sharedData;
        //53 is obtained from observation in YARN's ZKStateStore class
        _randomByteSize = 53;
        
        //calculate randomByte once only to minimize the calculationoverhead
        _rndAppByte = new byte[_randomByteSize];
        
        Random rnd = new Random(System.currentTimeMillis());
        rnd.nextBytes(_rndAppByte); 
        
        InputStream inStream = null;
        FileInputStream fileInStream = null;
        try {
            //try to load properties file from same path as the executable jar 
            fileInStream = new FileInputStream("zkndb-clusterj.properties");
            inStream = fileInStream;
        } catch (FileNotFoundException ex) {
            //if the file not found, load from the default properties file 
            //inside jar file
            inStream = ClassLoader.getSystemResourceAsStream(
                    "zkndb/storage/zkndb-clusterj.properties");
        }
           
        try {
            Properties props = new Properties();
            props.load(inStream);
            //create a session (connection to the database)
            _factory = ClusterJHelper.getSessionFactory(props);
            _session = _factory.getSession();
            //_session.deletePersistentAll(NdbApplicationStateCJ.class);
        } catch (FileNotFoundException ex){
            Logger.getLogger(NdbStorageImpl.class.getName()).log(Level.SEVERE, 
                    null, ex);
        } catch (IOException ex){
             Logger.getLogger(NdbStorageImpl.class.getName()).log(Level.SEVERE, 
                     null, ex);
        }
    }
    
    private void storeApplicationState()
    {
        NdbApplicationStateCJ storedApp = 
                _session.newInstance(NdbApplicationStateCJ.class);
        
        //to simplify the ID generation, random UUID is used
        long appId = UUID.randomUUID().getLeastSignificantBits();
        //Random rnd = new Random(System.currentTimeMillis());
        //int appIds = rnd.nextInt() + this._id;
        storedApp.setId(appId);
        storedApp.setAppState(_rndAppByte);
        
        _session.persist(storedApp);
        
        //when persist sucsses, _appId is updated, for reading purpose
        _appId = appId;
    }
    
    private void readApplicationState() throws InvalidDbEntryException
    {
        NdbApplicationStateCJ appState = 
                _session.find(NdbApplicationStateCJ.class, _appId);
        
        if(appState == null)
        {
            throw new InvalidDbEntryException(
                    _appId + " does not exist in ndb databases" );
        }
    }
    
    @PersistenceCapable(table = "applicationstate")
    public interface NdbApplicationStateCJ {

        @PrimaryKey
        long getId();
        void setId(long id);
	
	@Lob
        byte[] getAppState();
        void setAppState(byte[] context);
    }
    
}
