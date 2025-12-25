package lab.context.ragcraft.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.apiKey}")
    private String esApiKey;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return ElasticsearchClient.of(b -> b
                .host(esHost)
                .apiKey(esApiKey)
        );
    }

}