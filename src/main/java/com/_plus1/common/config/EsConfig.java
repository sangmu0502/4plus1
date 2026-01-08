package com._plus1.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class EsConfig {

    @Bean(destroyMethod = "close")
    public RestClient esRestClient(@Value("${app.es.host:http://localhost:9200}") String host) {
        return RestClient.builder(HttpHost.create(host)).build();
    }

    @Bean
    public ElasticsearchClient esClient(RestClient esRestClient, ObjectMapper objectMapper) {
        ElasticsearchTransport transport =
                new RestClientTransport(esRestClient, new JacksonJsonpMapper(objectMapper));
        return new ElasticsearchClient(transport);
    }
}