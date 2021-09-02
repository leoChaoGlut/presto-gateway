package personal.leo.presto.gateway.mapper.prestogateway;

import org.apache.ibatis.annotations.*;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;

import java.util.List;

@Mapper
public interface CoordinatorMapper {

    @Insert("insert into pg_coordinator(host,port,active) values(#{host},#{port},#{active})")
    int insert(CoordinatorPO coordinator);


    @Select("select * from pg_coordinator")
    List<CoordinatorPO> selectAll();

    @Select("select * from pg_coordinator where active=true")
    List<CoordinatorPO> selectActiveCoordinators();

    @Delete("delete from pg_coordinator where host=#{host} and port=#{port}")
    int remove(CoordinatorPO coordinator);

    @Update("update pg_coordinator set active=false where host=#{host} and port=#{port}")
    int deactive(CoordinatorPO coordinator);

    @Update("update pg_coordinator set active=true where host=#{host} and port=#{port}")
    int active(CoordinatorPO coordinator);
}
