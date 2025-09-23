package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setText(c.getText());
        dto.setAuthorName(c.getAuthor() != null ? c.getAuthor().getName() : null);
        dto.setCreated(c.getCreated());
        return dto;
    }

    public static Comment from(String text, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
    }
}