package ru.practicum.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))           // соединение (TCP)
                .setResponseTimeout(Timeout.ofSeconds(10))         // ожидание ответа (read)
                .setConnectionRequestTimeout(Timeout.ofSeconds(5)) // из пула соединений
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(httpClient);
        rf.setConnectTimeout(Duration.ofSeconds(5));
        rf.setConnectionRequestTimeout(Duration.ofSeconds(5));
        return new RestTemplate(rf);
    }
}
