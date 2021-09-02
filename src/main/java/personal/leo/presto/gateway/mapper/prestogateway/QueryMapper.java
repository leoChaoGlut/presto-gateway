package personal.leo.presto.gateway.mapper.prestogateway;

import org.apache.ibatis.annotations.*;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;

@Mapper
public interface QueryMapper {

    @Insert("insert into pg_query(query_id, coordinator_url, json) " +
            "values(#{query_id}, #{coordinator_url}, #{json})")
    int insert(QueryPO query);

    @Select("select coordinator_url " +
            "from pg_query " +
            "where query_id = #{query_id}")
    String selectCoordinatorUrl(@Param("query_id") String query_id);

    @Select("select * " +
            "from pg_query " +
            "where query_id = #{query_id}")
    QueryPO selectById(@Param("query_id") String query_id);

    @Delete("delete from pg_query " +
            "where create_time < DATE_ADD(NOW(), INTERVAL -10 DAY)")
    int cleanQuery();
}
