package zkndb.storage;

import com.mysql.clusterj.ClusterJHelper;
import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.SessionFactory;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import com.mysql.clusterj.annotation.Lob;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;

public class NdbStorageImpl extends Storage{
    
    List<Metric> _sharedData;
    private SessionFactory _factory;
    private Session _session;
    private int _id;
    private int _randomByteSize;

    private byte[] _rndAppByte;
    private long _appIds;
    
    public NdbStorageImpl(int id, List<Metric> shared){
        _sharedData = shared;
        _id = id;
        _randomByteSize = 1024;
        
        //calculate randomByte only only to minimize the overhead
        _rndAppByte = new byte[_randomByteSize];
        
        Random rnd = new Random(System.currentTimeMillis());
        rnd.nextBytes(_rndAppByte);  
    }
    
    @Override
    public void write() {
        try
        {
            ThroughputMetricImpl throughput = (ThroughputMetricImpl) _sharedData.get(_id);
            synchronized(throughput)
            {
                throughput.incrementRequests();
            }
            
            storeApplicationState();
            
            synchronized(throughput)
            {
                throughput.incrementAcks();
            }
        } 
        catch (Exception ex)
        {
            //assumptions: 
            //1. no exception is thrown during incrementing requests and acks
            //2. persists throws exception
            System.out.println("Exception in storeApplicationState");
            Logger.getLogger(NdbStorageImpl.class.getName()).log(Level.SEVERE, 
               null, ex);
        }
    }

    @Override
    public void read() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init() {
                //use default clusterj.properties included in this project
        File propsFile = new File("clusterj.properties"); 
        InputStream inStream;
        
        try {
            inStream = new FileInputStream(propsFile);
            Properties props = new Properties();
            props.load(inStream);
            //create a session (connection to the database)
            _factory = ClusterJHelper.getSessionFactory(props);
            _session = _factory.getSession();
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
        _appIds = UUID.randomUUID().getLeastSignificantBits();
        storedApp.setId(_appIds);
        storedApp.setAppState(_rndAppByte);
        
        _session.persist(storedApp);
    }
    
    private void readApplicationState()
    {
        QueryDomainType<NdbApplicationStateCJ> domainApp;
        Query<NdbApplicationStateCJ> queryApp;
        
        QueryBuilder builder = _session.getQueryBuilder();
        domainApp = builder.createQueryDefinition(NdbApplicationStateCJ.class);
        queryApp = _session.createQuery(domainApp);
        //resultsApp = queryApp.getResultList();
        
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
