**Note**: 
**The current application is not runnable and unit test needs to be added**

# Scalability

### Native solution

Architecture Diagram ![Data Tracker - Native](https://github.com/hexuanus/data-tracker/blob/main/img/Data%20Tracker%20-%20Native.png)

### M3 solution

Architecture Diagram ![Data Tracker - M3](https://github.com/hexuanus/data-tracker/blob/main/img/Data%20Tracker%20-%20M3.png)

The [M3](https://m3db.io/) platform aims to provide a turnkey, scalable, and configurable multi-tenant store for Prometheus, Graphite and other standard metrics schemas.
For M3 metrics, engineers can use M3 client [Tally](https://github.com/uber-java/tally) to define a metric with multiple metric tags in the application code.

e.g. volume metrics: `fetch name:volume crypto:BTC time_window:1m`  which will return 1 minute aggregation for volume of crypto BTC. These metrics will be emitted to the M3 metrics engine and stored in the M3 database.

The M3 query engine is provided to translate the M3QL from Grafana UI and search data points in the M3 database.


# Testing

### Unit Test

### Integration Test

Send an end to end request to the service endpoint and verify results. These requests can be catched from real traffic and stored in storage and be replayed at any time at staging/pre-production environments.

### Load Test

Measure service performance or how many requests the service can handle

### Black-box testing

A black box service sends a list of integration test requests against a production endpoint per minute and verifies the result. This is part of a health check besides heartbeat ping.

### Canary environment

Roll out the new binary to a pre production environment where fork the production traffic and verify metrics between latest and last known good binary.

### Monitoring and alerting

During gradual rollout to production, any application and system level alerts will trigger the deployment auto rollback


# Feature request

This is explained in native solution diagrams and M3 solution diagrams