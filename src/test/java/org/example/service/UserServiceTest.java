package org.example.service;

import org.example.dao.UserRepository;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }
    @Test
    void createUser_shouldReturnResponseDto() {
        UserRequestDto requestDto = new UserRequestDto("Иван", "ivan@mail.ru", 25);
        User savedUser = new User("Иван", "ivan@mail.ru", 25);
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void getUserById_shouldReturnUser_whenExists() {
        Long id = 1L;
        User user = new User("Иван", "ivan@mail.ru", 25);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals("Иван", result.getName());
        verify(userRepository, times(1)).findById(id);
    }
    @Test
    void getUserById_shouldThrowException_whenNotExists() {
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(id));
        verify(userRepository, times(1)).findById(id);
    }
    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        User user1 = new User("Иван", "ivan@mail.ru", 25);
        user1.setId(1L);
        User user2 = new User("Мария", "maria@mail.ru", 30);
        user2.setId(2L);
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }
    @Test
    void updateUser_shouldUpdateAndReturnDto_whenExists() {
        Long id = 1L;
        User existingUser = new User("Иван", "ivan@mail.ru", 25);
        existingUser.setId(id);
        UserRequestDto requestDto = new UserRequestDto("Иван Петров", "petrov@mail.ru", 26);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponseDto result = userService.updateUser(id, requestDto);

        assertEquals("Иван Петров", result.getName());
        assertEquals("petrov@mail.ru", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }
    @Test
    void updateUser_shouldThrowException_whenNotExists() {
        Long id = 999L;
        UserRequestDto requestDto = new UserRequestDto("Имя", "email@mail.ru", 20);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(id, requestDto));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void deleteUser_shouldCallDelete_whenExists() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }
    @Test
    void deleteUser_shouldThrowException_whenNotExists() {
        Long id = 999L;
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(id));
        verify(userRepository, never()).deleteById(any());
    }
}