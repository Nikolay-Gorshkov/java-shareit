package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseClient {
    private static final String HEADER_USER = "X-Sharer-User-Id";

    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${shareit.server.url}")
    private String serverUrl;

    public <T> ResponseEntity<T> get(String path, @Nullable Map<String, Object> params,
                                     @Nullable Long userId, Class<T> type) {
        return exchange(HttpMethod.GET, path, params, userId, null, type);
    }

    public <T> ResponseEntity<T> post(String path, @Nullable Long userId,
                                      @Nullable Object body, Class<T> type) {
        return exchange(HttpMethod.POST, path, null, userId, body, type);
    }

    public <T> ResponseEntity<T> patch(String path, @Nullable Long userId,
                                       @Nullable Object body, Class<T> type) {
        return exchange(HttpMethod.PATCH, path, null, userId, body, type);
    }

    public <T> ResponseEntity<T> delete(String path, @Nullable Long userId, Class<T> type) {
        return exchange(HttpMethod.DELETE, path, null, userId, null, type);
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method, String path,
                                           @Nullable Map<String, Object> params,
                                           @Nullable Long userId,
                                           @Nullable Object body, Class<T> type) {
        URI uri = buildUri(path, params);

        HttpHeaders headers = getHttpHeaders(method, userId, body);

        // Собираем HttpEntity так, чтобы при null body не включался chunked
        HttpEntity<?> request = (body == null)
                ? new HttpEntity<>(headers)
                : new HttpEntity<>(body, headers);

        log.info("GW → Server {} {} userId={} body={}", method, uri, userId, body);

        try {
            ResponseEntity<String> resp = rest.exchange(uri, method, request, String.class);

            log.info("Server → GW {} {} -> status={} length={}",
                    method, uri, resp.getStatusCodeValue(),
                    resp.getBody() == null ? 0 : resp.getBody().length());

            Object bodyObj = castBody(resp.getBody(), type);
            return ResponseEntity.status(resp.getStatusCode()).body(type.cast(bodyObj));

        } catch (HttpStatusCodeException e) {
            log.warn("Server → GW {} {} -> status={} body={}",
                    method, uri, e.getRawStatusCode(), e.getResponseBodyAsString());

            Object bodyObj = castBody(e.getResponseBodyAsString(), type);
            return ResponseEntity.status(e.getStatusCode()).body(type.cast(bodyObj));

        } catch (RestClientException e) {
            log.error("Server → GW {} {} -> transport error: {}", method, uri, e.toString());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(type == Object.class
                            ? type.cast(Map.of("error", "shareit-server is unavailable"))
                            : null);
        }
    }

    private static HttpHeaders getHttpHeaders(HttpMethod method, Long userId, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set(HEADER_USER, String.valueOf(userId));
        }
        if (method == HttpMethod.POST || method == HttpMethod.PATCH || method == HttpMethod.PUT) {
            if (body != null) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            } else {
                headers.setContentLength(0);
            }
        }
        return headers;
    }

    private URI buildUri(String path, @Nullable Map<String, Object> params) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(serverUrl + path);
        if (params != null) params.forEach(b::queryParam);
        return b.build().toUri();
    }

    @SuppressWarnings("unchecked")
    private <T> T castBody(String body, Class<T> type) {
        if (body == null) return null;
        try {
            if (type == String.class) return (T) body;
            if (type == Object.class) return (T) mapper.readValue(body, Object.class);
            return null;
        } catch (JsonProcessingException ex) {
            if (type == Object.class) return (T) Map.of("error", body);
            return (T) body;
        }
    }
}
