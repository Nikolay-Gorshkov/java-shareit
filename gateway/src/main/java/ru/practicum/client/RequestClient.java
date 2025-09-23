package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RequestClient extends BaseClient {
    public RequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, Object dto) {
        return post("/requests", userId, dto, Object.class);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get("/requests", null, userId, Object.class);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get("/requests/all", Map.of("from", from, "size", size), userId, Object.class);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/requests/" + requestId, null, userId, Object.class);
    }
}
