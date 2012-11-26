<pre>
zkndb is a simple storage benchmarking application.

It is composed by 3 packages: benchmark, metrics and storage.

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
  Metric contains the attributes that will be changed during the benchmark and are accessed by the MetricsEngine and Storage.
  MetricsEngine determines when and where to log the metrics.
  
storage package:
  Contains Storage.
  Contains the database dependent load generators (StorageImpl). 
  They perform the read and writes to the database and update the Metrics.
  
Synchronization is based on fine-grained locks since it is done at the Metric level. 
The benchmark has a list of Metrics, there is a separate metric for each Storage thread. 
MetricsEngine accesses this list within specific periods of time (argv[1]).
For now the only reason for the synchronization is because the MetricsEngine resets the metrics after logging so they do not overload.
</pre>