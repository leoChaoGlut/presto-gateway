# presto-gateway
presto-gateway


https://github.com/lyft/presto-gateway

prestosql提供的gateway存在连接泄漏问题.

比起prestosql提供的网关有更好的性能,更容易使用的api,代码更易读,易维护


### TODO list
- LB权重(目前轮询)
- Coordinator health 需要使用/ui/api/stats的结果来判断才能最准确
- ~~coordinator列表变更需要到多个presto-gateway上进行reload~~


### Tips
- 建议通过admin接口修改coordinator列表,如果直接修改数据库,可能导致缓存与数据库不一致