package zkndb.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import zkndb.benchmark.BenchmarkUtils;
import zkndb.exceptions.BadAclFormatException;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;
import zkndb.utils.ZKUtil;

/**
 *
 * @author 4knahs
 */
public class ZKStorageImpl extends Storage{
    
    //properties-file keys
    protected static final String ZK_HOST_PORT = "zkstorageimpl.zkhostport";
    protected static final String ZK_HOST_PORT_DEFAULT = "localhost:2184";
    
    protected static final String ZK_NODE_WORKING_PATH = "zkstorageimpl.zknodeworkingpath";
    protected static final String ZK_NODE_WORKING_PATH_DEFAULT = "";
    
    protected static final String ZK_SESSION_TIMEOUT ="zkstorageimpl.sessiontimeout";
    protected static final String ZK_SESSION_TIMEOUT_DEFAULT = "60000";
    
    protected static final String ZK_ACL_CONF = "zkstorageimpl.zkaclconf";
    protected static final String ZK_ACL_CONF_DEFAULT = "world:anyone:rwcda";
    
    protected static final String ROOT_ZNODE_NAME = "zkstorageimpl.rootznodename";
    protected static final String ROOT_ZNODE_NAME_DEFAULT = "ZKRMStateRoot";
    
    protected static final String NUM_RETRIES = "zkstorageimpl.numretries";
    protected static final String NUM_RETRIES_DEFAULT = "3";
    
    private static int _numRetries;
    
    private String _zkHostPort;
    private String _zkNodeWorkingPath;
    private int _zkSessionTimeout;
    private String _zkAclconf;
    private String _rootZnodeName;
    
    private ZooKeeper _zkClient;
    private ZooKeeper _oldZkClient;
    
    private List<ACL> _zkAcl;
    private String _zkRootNodePath;
    
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
                System.out.println("Exception in when trying to store application state");
                Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    @Override
    public void read() {
        ThroughputMetricImpl throughputMetric = null;
        throughputMetric = (ThroughputMetricImpl) _sharedData.get(_id);
        synchronized(throughputMetric){
            try {
                throughputMetric.incrementRequests();
                readApplicationState();
                throughputMetric.incrementAcks();
            } catch (Exception ex) {
                System.out.println("Exception in when trying to read application state");
                Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    @Override
    public void init() {
        _sharedData = BenchmarkUtils.sharedData;
        _randomByteSize = 53;
        _rndAppByte = new byte[_randomByteSize];
        
        Random rnd = new Random(System.currentTimeMillis());
        rnd.nextBytes(_rndAppByte);
        
        InputStream inStream = ClassLoader.getSystemResourceAsStream(
                "zkndb/storage/zkndb-zkstorageimpl.properties");
        
        Properties props = new Properties();
        try {
            props.load(inStream);
            
            _zkHostPort = props.getProperty(ZKStorageImpl.ZK_HOST_PORT, 
                    ZKStorageImpl.ZK_HOST_PORT_DEFAULT);
            _zkNodeWorkingPath = props.getProperty(ZKStorageImpl.ZK_NODE_WORKING_PATH, 
                    ZKStorageImpl.ZK_NODE_WORKING_PATH_DEFAULT);
            _zkSessionTimeout = Integer.parseInt(props.getProperty(
                    ZKStorageImpl.ZK_SESSION_TIMEOUT, ZKStorageImpl.ZK_SESSION_TIMEOUT_DEFAULT));
            _zkAclconf = props.getProperty(ZKStorageImpl.ZK_ACL_CONF, 
                    ZKStorageImpl.ZK_ACL_CONF_DEFAULT);
            _rootZnodeName = props.getProperty(ZKStorageImpl.ROOT_ZNODE_NAME, 
                    ZKStorageImpl.ROOT_ZNODE_NAME_DEFAULT);
            _numRetries = Integer.parseInt(props.getProperty(ZKStorageImpl.NUM_RETRIES, 
                    ZKStorageImpl.NUM_RETRIES_DEFAULT));
            
        } catch (IOException ex) {
            //use default properties set at the beginning
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, 
                    null, ex);
            System.out.println("Program can't continue because initialization "
                    + "from properties file fails :(");
            return;
        }

        List<ACL> zkConfAcls = null;
        try {
            _zkAclconf = ZKUtil.resolveConfIndirection(_zkAclconf);
             zkConfAcls = ZKUtil.parseACLs(_zkAclconf);
        } catch (IOException ex) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (BadAclFormatException bex){
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, bex);
        }
        
        if(zkConfAcls != null && zkConfAcls.isEmpty()){
            zkConfAcls = Ids.OPEN_ACL_UNSAFE;
        }
        
        _zkAcl = zkConfAcls;
        _zkRootNodePath = _zkNodeWorkingPath + "/" + _rootZnodeName;
        
        try {
            createConnection();
            //version -1 means zkclient will match any node's versions
           // deleteWithRetries(_zkRootNodePath, 0);
        } catch (IOException ex) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            createWithRetries(_zkRootNodePath, null, _zkAcl, CreateMode.PERSISTENT);
        } catch (KeeperException ke) {
            if (ke.code() != KeeperException.Code.NODEEXISTS) {
                //alternative is to throw the exception
                Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, ke);
            }
        } catch (Exception ex) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void storeApplicationState() throws Exception
    {
        long appId = UUID.randomUUID().getLeastSignificantBits();
        storeApplicationState(String.valueOf(appId), _rndAppByte);
        
        //when storing success, _appId is updated, for reading purpose
        _appId = appId;
    }
    
    private synchronized void storeApplicationState(String appId, 
            byte[] appStateData) throws Exception
    {
        String nodeCreatePath = getNodePath(appId);
              
        try {
            // currently throw all exceptions. May need to respond differently for HA 
            // based on whether we have lost the right to write to ZK
            createWithRetries(nodeCreatePath, appStateData,
                    _zkAcl, CreateMode.PERSISTENT);
        } catch (Exception e) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE,
                    "Error storing info for app: {0}. Error: {1}",
                    new Object[]{appId, e.toString()});

            throw e;
        }
    }
    
    private void readApplicationState() throws Exception
    {
        loadApplicationState(String.valueOf(_appId));
    }
    
    private synchronized void removeApplicationState(String appId)
            throws Exception {
        String nodeRemovePath = getNodePath(appId);
        Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.INFO,
                "Removing info for app: {0} at: {1}", new Object[]{appId, nodeRemovePath});
        try {
            deleteWithRetries(nodeRemovePath, 0);
        } catch (Exception e) {
            Logger.getLogger(ZKStorageImpl.class.getName()).log(Level.SEVERE, 
                    "Error removing info for app: {0}. Erorr: {1}", 
                    new Object[]{appId, e.toString()});
        }
    }
    
    private synchronized void loadApplicationState(String appId) 
            throws Exception
    {
        String loadedNodePath = getNodePath(appId);
        byte[] nodeData = getDataWithRetries(loadedNodePath, false);
    }
      // ZK related code
  /**
   * Watcher implementation which forward events to the ZKRMStateStore
   * This hides the ZK methods of the store from its public interface 
   */
    private final class ForwardingWatcher implements Watcher {

        ZooKeeper zk;

        public ForwardingWatcher(ZooKeeper zk) {
            this.zk = zk;
        }

        @Override
        public void process(WatchedEvent event) {
            try {
                ZKStorageImpl.this.processWatchEvent(zk, event);
            } catch (Throwable t) {
                Logger.getLogger(ZKStorageImpl.class.getName()).log(
                        Level.SEVERE, "Failed to process watcher event {0}: {1}", 
                        new Object[]{event, t.toString()});
            }
        }
    }
    
    private synchronized void processWatchEvent(ZooKeeper zk, WatchedEvent event)
            throws Exception {
        Watcher.Event.EventType eventType = event.getType();
        Logger.getLogger(ZKStorageImpl.class.getName()).log(
                Level.INFO, "Watcher event type: {0} with state: {1} for "
                + "path:{2} for {3}", 
                new Object[]{eventType, event.getState(), event.getPath(), this});

        if (eventType == Watcher.Event.EventType.None) {
            // the connection state has changed
            switch (event.getState()) {
                case SyncConnected:
                   // LOG.info("ZKRMStateStore Session connected");
                    if (_oldZkClient != null) {
                        // the SyncConnected must be from the client that sent Disconnected
                        assert _oldZkClient == zk;
                        _zkClient = _oldZkClient;
                        _oldZkClient = null;
                        //LOG.info("ZKRMStateStore Session restored");
                    }
                    break;
                case Disconnected:
                    //LOG.info("ZKRMStateStore Session disconnected");
                    _oldZkClient = _zkClient;
                    _zkClient = null;
                    break;
                case Expired:
                    // the connection got terminated because of session timeout
                    // call listener to reconnect
                    //LOG.info("Session expired");
                    createConnection();
                    break;
                default:
                   // LOG.error("Unexpected Zookeeper watch event state: " + event.getState());
                    break;
            }
        }
    }
  
    
    private void createConnection() throws IOException, Exception
    {
        if (_zkClient != null) {
            try {
                _zkClient.close();
            } catch (InterruptedException e) {
                throw new IOException("Interrupted while closing ZK", e);
            }
            _zkClient = null;
        }
        
        if (_oldZkClient != null) {
            try {
                _oldZkClient.close();
            } catch (InterruptedException e) {
                throw new IOException("Interrupted while closing old ZK", e);
            }
            _oldZkClient = null;
        }
        
        _zkClient = getNewZooKeeper();
    }
    
    private synchronized ZooKeeper getNewZooKeeper() throws Exception {
        ZooKeeper zk = new ZooKeeper(_zkHostPort, _zkSessionTimeout, null);
        zk.register(new ForwardingWatcher(zk));
        return zk;
    }
    
    private String getNodePath(String nodeName) {
        return (_zkRootNodePath + "/" + nodeName);
    }
    
    private String createWithRetries(final String path, final byte[] data, 
            final List<ACL> acl, final CreateMode mode) throws KeeperException, Exception
    {
        return zkDoWithRetries(new ZKAction<String>(){
            @Override
            public String run () throws KeeperException, InterruptedException{
                return _zkClient.create(path, data, acl, mode);
            }
        });
    }
    
    private void deleteWithRetries(final String path, final int version)
            throws Exception {
        zkDoWithRetries(new ZKAction<Void>() {
            @Override
            public Void run() throws KeeperException, InterruptedException {
                _zkClient.delete(path, version);
                return null;
            }
        });
    }
    
    private byte[] getDataWithRetries(final String path, final boolean watch)
            throws Exception {
        return zkDoWithRetries(new ZKAction<byte[]>() {
            @Override
            public byte[] run() throws KeeperException, InterruptedException {
                Stat stat = new Stat();
                return _zkClient.getData(path, watch, stat);
            }
        });
    }

    
    private static <T> T zkDoWithRetries(ZKAction<T> action)
            throws Exception {
        int retry = 0;
        while (true) {
            try {
                return action.runWithCheck();
            } catch (KeeperException ke) {
                if (shouldRetry(ke.code()) && ++retry < _numRetries) {
                    continue;
                }
                throw ke;
            }
        }
    }
    
    private abstract class ZKAction<T> {

        abstract T run() throws KeeperException, InterruptedException;

        T runWithCheck() throws Exception {
            long startTime = System.currentTimeMillis();
            while (_zkClient == null) {
                ZKStorageImpl.this.wait(_zkSessionTimeout);
                if (_zkClient != null) {
                    break;
                }
                if (System.currentTimeMillis() - startTime > _zkSessionTimeout) {
                    throw new Exception("Wait for ZKClient creation timed out");
                }
            }
            return run();
        }
    }
    
    private static boolean shouldRetry(KeeperException.Code code) {
        switch (code) {
            case CONNECTIONLOSS:
            case OPERATIONTIMEOUT:
                return true;
        }
        return false;
    }
}
