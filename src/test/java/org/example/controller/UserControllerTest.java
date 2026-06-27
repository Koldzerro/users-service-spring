package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Иван", "ivan@mail.ru", 25);
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван", "ivan@mail.ru", 25, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Long id = 1L;
        UserResponseDto responseDto = new UserResponseDto(id, "Иван", "ivan@mail.ru", 25, LocalDateTime.now());
        when(userService.getUserById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Иван", "ivan@mail.ru", 25, LocalDateTime.now());
        UserResponseDto user2 = new UserResponseDto(2L, "Мария", "maria@mail.ru", 30, LocalDateTime.now());
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].name").value("Мария"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        Long id = 1L;
        UserRequestDto requestDto = new UserRequestDto("Иван Петров", "petrov@mail.ru", 26);
        UserResponseDto responseDto = new UserResponseDto(id, "Иван Петров", "petrov@mail.ru", 26, LocalDateTime.now());
        when(userService.updateUser(eq(id), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("petrov@mail.ru"));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isOk());
    }
}