# presto-gateway
presto-gateway


https://github.com/lyft/presto-gateway

prestosql提供的gateway存在连接泄漏问题.

比起prestosql提供的网关有更好的性能,更容易使用的api,代码更易读,易维护


### TODO list
- LB权重(目前轮询)
- ~~Coordinator health 需要使用/ui/api/stats的结果来判断才能最准确~~
- ~~coordinator列表变更需要到多个presto-gateway上进行reload~~
- ~~增加completion_time,query完成时,异步更新completion_time~~
- ~~coordinator list没有自动更新监控状态~~
- ~~query 发送到MQ,不存储在mysql~~
- ~~query完成时,仅获取到x-presto-user,后续可以考虑从query开始时,获取更多header信息,如user-agent,x-presto-source等~~

### Tips
- 建议通过admin接口修改coordinator列表,如果直接修改数据库,可能导致缓存与数据库不一致

### Warnings
- 分支与presto版本保持一致,如果不保持一致,可能会因presto升级,导致gateway部分功能出现异常,如presto coordinator 健康检查,presto 请求头变更

### Release Notes:
345-kafka:
- 增加presto delete 方法,支持客户端cancel query
- 支持query信息写入kafka,做后续查询统计

