# mqtt-jdbc-rapid-connector

[DOC](README.md) | [文档](README_zh.md)

A stable server for receiving messages from mqtt, and translating to jdbc.  
Using api can change different datasource,topic or table immediately and persistently.  
Distributed through shared subscriptions.

Support a variety of JDBC compatible databases, including MySQL and Clickhouse.

## QuickStart

*Setting a basic configuration is in need before starting.*

```
POST /api/setTopic
 
/api/setTopic?topic=dbc_test&share=$share/dbc/&table=dbc_test&column="id","number"&type=Number,String
```

|parameters|remark|example|required|
|---|---|---|---|
|topic|topic name|dbc_test|√|
|share|use share subscribe or not|$share/dbc/||
|table|table name|dbc_test|√|
|column|column content|id,name|√|
|type|column type|Number,String|√|
|*datastore|-|-|-|
*: in developing.

*Now we can use the mqtt to insert data into db.*  
send to mqtt topic.
- single
```json
{
  "type": "single",
  "table": "dbc_test",
  "data": [1,"a"]
}
```
- multiple
```json
{
  "type": "multiple",
  "table": "dbc_test",
  "data": [[1,"a"],[1,"a"]]
}
```
## api
### GET  /api/hello
A test api.
### GET  /api/getApi
Get all api.
### POST /api/setTopic
Set a topic data.  
Parameters: See [QuickStart](#QuickStart)
### GET  /api/rmTopic
Remove a topic.
```
POST /api/rmTopic

/api/setTopic?name=dbc_test
```

|parameters|remark|example|required|
|---|---|---|---|
|name|topic name|dbc_test|√|

### GET  /api/getConnection
Get all connections.
### GET  /api/getDataSource
Get all datasource.

## performance
A simple test.

|insert model|total time|average time|
|---|---|---|
|1ThreadLoop 10,000|2.469s|0.2469ms|
|1ThreadLoop 100,000|9.852s|0.09852ms|
|1ThreadLoop 1,000,000|64.307s|0.064307ms|

## Configuration
See `pom.xml` and `application-*.yml`.
### Add a maven profile
Create a new yml profile and add tags in pom.xml.  
Refresh the idea maven profile and life cycle.
### YML configuration
- `management.server.port` spring actuator port.`:port/actuator`
- `spring.boot.admin.client.instance.service-url: {management.server.port}` &`spring.boot.admin.client.instance.url: {management.server.port}` spring admin.
#### datasource
~~hikari is used for database connection pool.~~  
master is required.  
clusters are other datasource.
- `maximum-pool-size` how many connections are allowed.
- `master.url` datasource url.
- cluster is an array`cluster.-.url`.
### Note
- Use `maximum-pool-size` configuration and distribution can improve the performance.
- For distribution, alter `mqtt.clientId` is in need.
