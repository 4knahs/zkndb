<h3>ZKNDB framework</h3>
zkndb is a simple storage benchmarking application. So far it has implementations for HDFS, 
Zookeeper and NDB MySQL.
It is composed by 3 packages: benchmark, metrics and storage.

<h3>Packages</h3>
<pre>
benchmark package: 
  Contains Benchmark.
  This package contains the different benchmark applications (BenchmarkImpl). 
  It is responsible for creating and running the metrics and storage.
  A BenchmarkImpl should receive the following arguments:
    argv[0] : Number of StorageImpl threads to run
    argv[1] : Period in between metric logging (ms)
    argv[2] : Execution time (ms)
    argv[3] : Time per cycle (ms)
  Optional arguments:
    argv[4] : Number of writes per cycle
    argv[5] : Number of reads per cycle
  
metrics package:
  Contains Metric and MetricsEngine.
  This package contains the different metrics (MetricImpl) and metric logging logic (EngineImpl).
  Metric contains the attributes that will be changed during the benchmark and are accessed by the MetricsEngine 
  and Storage.
  MetricsEngine determines when and where to log the metrics.
  
storage package:
  Contains Storage.
  Contains the database dependent load generators (StorageImpl). 
  They perform the read and writes to the database and update the Metrics.
</pre>

<h3>Application Example</h3>
In the following example one can implement both the metrics and storage system independently.
<pre>
/*Main of DummyBenchmarkImpl.java*/
public static void main(String[] args){
        
        /*Reads the inputs*/
        BenchmarkUtils.readInput(args);

        /*Sets the wanted metrics*/
        BenchmarkUtils.setMetric("ThroughputMetricImpl");
    
        /*Engine that aggregates the results in periods of argv[1] (ms)*/    
        BenchmarkUtils.setEngine("ThroughputEngineImpl");

        /*Database specific implementation*/
        BenchmarkUtils.setStorage("DummyStorageImpl");
        
        /*Runs the StorageImplementation in as many threads as specified in arg[0]*/
        BenchmarkUtils.run();
    }
</pre>
  
<h3>Synchronization</h3>  
<pre>
<del>Synchronization is based on fine-grained locks since it is done at the Metric level. 
  The benchmark has a list of Metrics, there is a separate metric for each Storage thread. 
  MetricsEngine accesses this list within specific periods of time (argv[1]).
  For now the only reason for the synchronization is because the MetricsEngine resets the metrics after
  logging so they do not overload. </del> There is no locking, the MetricsEngine performs only reads,
  this means that for now, the lenght of requests is limited by 32 bytes (long).
</pre>

<h3>Possible future work</h3>
<pre>
  <del>In order to implement this without intermediate locking, instead of the MetricsEngine,
  the StorageImpl should periodically store the metrics themselves.
  This way the MetricEngine would only need to aggregate the results in the end of the execution.
  The threads could be reasonaby synchronized in terms of time periods.</del> Under development.
</pre>

<h3>Current issues</h3>
<pre>
  <del>MetricsEngine does not perform a lock on the whole list of metrics. This means that it has to acquire
  a lock for each of its elements separately. This introduces a delay in between the metrics periods
  and might potentially misplace them relative to each others. This delay might correspond
  to the time it takes to perform a single read or write and so it is dependent on the size of the data being
  R/W.</del> No locking.
</pre>