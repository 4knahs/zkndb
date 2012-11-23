zkndb is a storage benchmarking application.

It is composed by 3 packages: benchmark, metrics and storage.

benchmark package: 
  This package contains the different benchmark applications. 
  It is responsible for creating and running the metrics and storage.
  
metrics package:
  Contains Metric and MetricsEngine. 
  Metric contains the attributes that will be changed during the benchmark and are accessed by the MetricsEngine and Storage.
  MetricsEngine determines when and where to log the metrics.
  
storage package:
  Contains the database dependent load generators. 
  They perform the read and writes to the database and update the Metrics.
  
Synchronization is done at the Metric level, 
so the benchmark will have a list of Metrics, where each metric is only accessed by its Storage thread and from time to time, by the MetricsEngine.