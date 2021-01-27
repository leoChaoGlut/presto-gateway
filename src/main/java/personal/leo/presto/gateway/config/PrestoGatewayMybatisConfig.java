package personal.leo.presto.gateway.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "personal.leo.presto.gateway.mapper.prestogateway", sqlSessionTemplateRef = "prestoGatewaySqlSessionTemplate")
public class PrestoGatewayMybatisConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.presto-gateway")
    public DataSource prestoGatewayDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory prestoGatewaySqlSessionFactory(DataSource prestoGatewayDatasource) throws Exception {
        final org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
//        config.setMapUnderscoreToCamelCase(true);

        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(prestoGatewayDatasource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfiguration(config);

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate prestoGatewaySqlSessionTemplate(SqlSessionFactory prestoGatewaySqlSessionFactory) {
        return new SqlSessionTemplate(prestoGatewaySqlSessionFactory);
    }
}
