package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;              // <— DI
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail(), null);
        User u = UserMapper.fromDto(dto);
        u.setId(seq.incrementAndGet());
        repo.save(u);
        return UserMapper.toDto(u);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User u = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail(), id);
            u.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) u.setName(dto.getName());
        repo.save(u);
        return UserMapper.toDto(u);
    }

    @Override public UserDto get(Long id) {
        return UserMapper.toDto(repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id)));
    }

    @Override public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override public void delete(Long id) { repo.deleteById(id); }

    private void validateEmail(String email, Long ignoreId) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Invalid email"); // 400
        }
        if (repo.existsEmail(email, ignoreId)) {
            throw new ConflictException("Email already in use"); // 409
        }
    }
}