package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> add(Long userId, Object dto) {
        return post("/items", userId, dto, Object.class);
    }

    public ResponseEntity<Object> patch(Long userId, Long itemId, Map<String, Object> patch) {
        return patch("/items/" + itemId, userId, patch, Object.class);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/items/" + itemId, null, userId, Object.class);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId) {
        return get("/items", null, userId, Object.class);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/items/search", Map.of("text", text), null, Object.class);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object dto) {
        return post("/items/" + itemId + "/comment", userId, dto, Object.class);
    }

}
