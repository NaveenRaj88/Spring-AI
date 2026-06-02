package org.spring.ai.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;


@Configuration
public class SpringAIConfiguration {

    @Bean
    public RestClientCustomizer logbookCustomizer(LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor){
        return restClient -> restClient.requestInterceptor(logbookClientHttpRequestInterceptor);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
