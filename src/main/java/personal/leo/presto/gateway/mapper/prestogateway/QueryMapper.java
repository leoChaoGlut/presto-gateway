package personal.leo.presto.gateway.mapper.prestogateway;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;

import java.util.List;

@Mapper
public interface QueryMapper {

    @Insert("insert into query(query_id,coordinator_url) values(#{query_id},#{coordinator_url})")
    int insert(QueryPO query);


    @Select("select * from query")
    List<CoordinatorPO> selectAll();

    @Select("select coordinator_url from query where query_id = #{query_id}")
    String selectCoordinatorUrl(@Param("query_id") String query_id);
}
