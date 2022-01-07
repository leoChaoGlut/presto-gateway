package personal.leo.presto.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.function.Predicate;

@EnableOpenApi
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
//                .alternateTypeRules(
//                        AlternateTypeRules.newRule(OVertex.class, Object.class)
//                )
                .select()
                .paths(paths())
                .build();
    }

    private Predicate<String> paths() {
        return PathSelectors.ant("/*/**")
                .and(PathSelectors.ant("/error/**").negate());
    }
}
