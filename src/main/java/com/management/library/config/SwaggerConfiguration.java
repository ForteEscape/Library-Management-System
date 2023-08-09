package com.management.library.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/css/**").addResourceLocations("classpath:/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
    registry.addResourceHandler("/images/**").addResourceLocations("classpath:/images/");
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .securitySchemes(Collections.singletonList(apiKey()))
        .securityContexts(Collections.singletonList(securityContext()))
        .ignoredParameterTypes(Pageable.class)
        .consumes(getConsumeContentTypes())
        .produces(getProduceContentType())
        .apiInfo(getApiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.management.library.controller"))
        .paths(PathSelectors.ant("/**"))
        .build();
  }

  private ApiKey apiKey(){
    return new ApiKey("apiKey", "Authorization", "header");
  }

  // request content type setting
  private Set<String> getConsumeContentTypes() {
    Set<String> consumes = new HashSet<>();
    consumes.add("application/json;charset=UTF-8");

    return consumes;
  }

  // response content type setting
  private Set<String> getProduceContentType() {
    Set<String> produces = new HashSet<>();
    produces.add("application/json;charset=UTF-8");

    return produces;
  }

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder()
        .title("API")
        .description("Library-Management-System API Document")
        .contact(
            new Contact("L.M.S Swagger", "https://github.com/ForteEscape/Library-Management-System",
                "BNG")
        )
        .version("1.0")
        .build();
  }

  private SecurityContext securityContext() {
    return springfox.documentation.spi.service.contexts.SecurityContext
        .builder()
        .securityReferences(securityReference()).forPaths(PathSelectors.any())
        .build();
  }

  private List<SecurityReference> securityReference(){
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;

    return Collections.singletonList(new SecurityReference("apiKey", authorizationScopes));
  }
}
