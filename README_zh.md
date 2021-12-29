# mqtt-jdbc-rapid-connector

[DOC](README.md) | [文档](README_zh.md)

一个稳定可靠的写数据服务，通过接受mqtt信息，解析到jdbc写入数据库。  
使用web api可以快速设置数据源、主题和表的格式，并持久化。  
通过共享订阅实现分布式。

支持兼容jdbc的多种数据库，包括mysql和clickhouse等。

## 快速开始

*在使用前需要设置主题和表的信息。*

```
POST /api/setTopic
 
/api/setTopic?topic=dbc_test&share=$share/dbc/&table=dbc_test&column="id","number"&type=Number,String
```

|参数|备注|示例|必填|
|---|---|---|---|
|topic|主题名|dbc_test|√|
|share|是否使用共享订阅|$share/dbc/||
|table|表名|dbc_test|√|
|column|列名|id,name|√|
|type|列类型|Number,String|√|
|*datastore|-|-|-|
*: in developing.

*现在向mqtt主题推送信息即可。*  
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
测试api。
### GET  /api/getApi
获取全部api。
### POST /api/setTopic
设置一个主题。  
参数: 见 [快速开始](#快速开始)
###GET  /api/rmTopic
移除一个主题。
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

## 表现测试
一个简单的测而。

|写入模式|完成总时间|平均时间|
|---|---|---|
|1ThreadLoop 10,000|2.469s|0.2469ms|
|1ThreadLoop 100,000|9.852s|0.09852ms|
|1ThreadLoop 1,000,000|64.307s|0.064307ms|

## 配置
参考 `pom.xml` 和 `application-*.yml`。
### 添加maven和yml
创建一个yml配置，并在pom.xml中添加标签。  
在IDE中刷新maven配置。
### YML配置
- `management.server.port` spring actuator 端口.`:port/actuator`
- `spring.boot.admin.client.instance.service-url: {management.server.port}` &`spring.boot.admin.client.instance.url: {management.server.port}` spring admin.
#### datasource
~~hikari 用作数据源的连接池。~~  
master 主数据源分支是必须的。  
clusters 是其让数据源分支。
- `maximum-pool-size` 每个连接池允许的。
- `master.url` 主数据源的url。
- cluster 分支是一个yml格式的数组`cluster.-.url`。
### 注意
- 使用 `maximum-pool-size` 配置和分布式订阅的分布式部署可以提升应用性能。
- 使用分布式订阅时，修改 `mqtt.clientId` 是必要的。
