package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    public UserDto create(UserDto dto) {
        validateEmail(dto.getEmail(), null);
        User u = UserMapper.fromDto(dto);
        u = repo.save(u);
        return UserMapper.toDto(u);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User u = getOrThrow(id);
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            validateEmail(dto.getEmail(), id);
            u.setEmail(dto.getEmail());
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            u.setName(dto.getName());
        }
        u = repo.save(u);
        return UserMapper.toDto(u);
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(getOrThrow(id));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        repo.deleteById(id);
    }

    private User getOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private void validateEmail(String email, Long ignoreId) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Invalid email");
        }
        boolean exists = (ignoreId == null)
                ? repo.existsByEmailIgnoreCase(email)
                : repo.existsByEmailIgnoreCaseAndIdNot(email, ignoreId);
        if (exists) throw new ConflictException("Email already in use");
    }
}
