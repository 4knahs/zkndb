/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zkndb.storage;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import zkndb.benchmark.BenchmarkUtils;
import zkndb.metrics.Metric;
import zkndb.metrics.ThroughputMetricImpl;

/**
 *
 * @author 4knahs
 */
public class HdfsStorageImpl extends Storage {

    private FileSystem fs = null;
    private Path fsWorkingPath;
    private Path fsRootDirPath;
    private static final String ROOT_DIR_NAME = "/yarn";
    private long uuid;
    private int blockSize = 53;
    private byte[] block = new byte[blockSize];
    private boolean useHDFS = false;

    @Override
    public void init() {
        _sharedData = BenchmarkUtils.sharedData;
        _requestRate = BenchmarkUtils.requestRate;

        System.out.println("Storage " + _id + " establishing connection");

        Configuration conf = new YarnConfiguration();

        conf.set(YarnConfiguration.FS_RM_STATE_STORE_URI, "hdfs://localhost:9000/");
        
        fsWorkingPath = new Path("hdfs://localhost:9000/");
        fsRootDirPath = new Path(fsWorkingPath, ROOT_DIR_NAME);
        
        try {
            // create filesystem
            fs = fsWorkingPath.getFileSystem(conf);
            fs.mkdirs(fsRootDirPath);
        } catch (IOException ex) {
            Logger.getLogger(HdfsStorageImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void write() {
        Metric metric = _sharedData.get(_id);
        long new_uuid = uuid;
        
        synchronized (metric) {
            ((ThroughputMetricImpl) metric).incrementRequests();
            try {
                //Do write to datastore
                new_uuid = UUID.randomUUID().getLeastSignificantBits();
                Path nodeCreatePath = getNodePath(String.valueOf(new_uuid));
                
                try {
                    // currently throw all exceptions. May need to respond differently for HA
                    // based on whether we have lost the right to write to FS
                    writeFile(nodeCreatePath, block);
                    uuid = new_uuid;
                } catch (Exception e) {
                    throw e;
                }

                
                ((ThroughputMetricImpl) metric).incrementAcks();
            } catch (Exception e) {
                //Sent request but it could not be served.
                //Should catch only specific exception.
            }
        }
    }

    @Override
    public void read() {
        synchronized (_sharedData.get(_id)) {
            ((ThroughputMetricImpl) _sharedData.get(_id)).incrementRequests();
            try {
                //Do read to datastore
                Path nodeCreatePath = getNodePath(String.valueOf(uuid));
                readFile(nodeCreatePath, blockSize);
                //System.out.println("Storage " + _id + " random read.");
                ((ThroughputMetricImpl) _sharedData.get(_id)).incrementAcks();
            } catch (Exception e) {
                //Sent request but it could not be served.
                //Should catch only specific exception.
            }
        }
    }

    private void deleteFile(Path deletePath) throws Exception {
        if (!fs.delete(deletePath, true)) {
            throw new Exception("Failed to delete " + deletePath);
        }
    }

    private byte[] readFile(Path inputPath, long len) throws Exception {
        FSDataInputStream fsIn = fs.open(inputPath);
        // state data will not be that "long"
        byte[] data = new byte[(int) len];
        fsIn.readFully(data);
        return data;
    }

    private void writeFile(Path outputPath, byte[] data) throws Exception {
        FSDataOutputStream fsOut = fs.create(outputPath, false);
        fsOut.write(data);
        fsOut.flush();
        fsOut.close();
    }

    private Path getNodePath(String nodeName) {
        return new Path(fsRootDirPath, nodeName);
    }
}
