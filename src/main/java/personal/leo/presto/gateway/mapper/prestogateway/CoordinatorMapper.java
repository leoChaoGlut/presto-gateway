package personal.leo.presto.gateway.mapper.prestogateway;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;

import java.util.List;

@Mapper
public interface CoordinatorMapper {

    @Insert("insert into coordinator(host,port) values(#{host},#{port})")
    int insert(@Param("host") String host, @Param("port") int port);


    @Select("select host,port from coordinator")
    List<CoordinatorPO> selectAll();
}
