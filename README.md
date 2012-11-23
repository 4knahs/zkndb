zkndb is a storage benchmarking application.

It is composed by 3 packages: benchmark, metrics and storage.

benchmark package: 
  Contains Benchmark.
  This package contains the different benchmark applications (BenchmarkImpl). 
  It is responsible for creating and running the metrics and storage.
  A BenchmarkImpl should receive the following arguments:
    argv[0] : Number of StorageImpl threads to run
    argv[1] : Period in between metric logging
    argv[2] : Execution time (ms)
  
metrics package:
  Contains Metric and MetricsEngine.
  This package contains the different metrics (MetricImpl) and metric logging logic (EngineImpl).
  Metric contains the attributes that will be changed during the benchmark and are accessed by the MetricsEngine and Storage.
  MetricsEngine determines when and where to log the metrics.
  
storage package:
  Containt Storage.
  Contains the database dependent load generators (StorageImpl). 
  They perform the read and writes to the database and update the Metrics.
  
Synchronization is done at the Metric level, so the benchmark will have a list of Metrics,
where each metric is only accessed by its Storage thread and from time to time, by the MetricsEngine.