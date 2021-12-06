/*
package com.glx.xuan.demo.conf;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean enableSwagger;
    @Value("${swagger.serviceUrl}")
    private String serviceUrl;
    @Value("${swagger.contact}")
    private String contact;
    @Value("${swagger.title}")
    private String title;
    @Value("${swagger.version}")
    private String version;
    @Value("${swagger.basePackage}")
    private String basePackage;
    @Value("${swagger.description}")
    private String description;
    @Bean
    public Docket createRestApi() {

       */
/* ParameterBuilder tokenpar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenpar.name("AuthToken").description("AuthToken")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(tokenpar.build());    //根据每个方法名也知道当前方法在设置什么参数*//*


        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                //basePage就是具体要扫描的包，有swagger注解的方法就生成到文档里面去
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
//                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .termsOfServiceUrl(serviceUrl)
                .contact(contact)
                .version(version)
                .build();
    }
}*/
