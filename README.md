**Note**: 
**The current application is not runnable and unit test needs to be added**

#Scalability: 
TODO: use M3DB (https://m3db.io/) to store the time series data, and provide Grafana(https://grafana.com/) dashboard to users for query

#Testing:
unit test + load test + performance metrics

#Feature request
steps: 
1. Send the price information to a Kafka message queue
2. Create a service to use flink/spark job to do realtime aggregation per alerting rule
3. Send pager duty/email if the aggregated results hits threshold.