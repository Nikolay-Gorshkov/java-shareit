package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient extends BaseClient {

    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Object dto) {
        return post("/users", null, dto, Object.class);
    }

    public ResponseEntity<Object> patch(Long id, Object patch) {
        return patch("/users/" + id, null, patch, Object.class);
    }

    public ResponseEntity<Object> get(Long id) {
        return get("/users/" + id, null, null, Object.class);
    }

    public ResponseEntity<Object> getAll() {
        return get("/users", null, null, Object.class);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete("/users/" + id, null, Object.class);
    }
}
