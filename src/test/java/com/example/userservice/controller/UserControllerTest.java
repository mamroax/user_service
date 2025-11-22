package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserControllerTest {

    @Test
    void testGetAll() throws Exception {
        UserService service = mock(UserService.class);
        when(service.findAll()).thenReturn(List.of(
                UserDto.builder().id(1L).username("A").email("a@mail.com").age(20).build()
        ));

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("A"));

        verify(service).findAll();
    }

    @Test
    void testGetById() throws Exception {
        UserService service = mock(UserService.class);
        when(service.findById(1L)).thenReturn(
                UserDto.builder().id(1L).username("B").email("b@mail.com").age(30).build()
        );

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();

        mvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("b@mail.com"));

        verify(service).findById(1L);
    }

    @Test
    void testCreate() throws Exception {
        UserService service = mock(UserService.class);
        when(service.create(any())).thenReturn(
                UserDto.builder().id(1L).username("C").email("c@mail.com").age(25).build()
        );

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "C", "email": "c@mail.com", "age": 25}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).create(any());
    }

    @Test
    void testUpdate() throws Exception {
        UserService service = mock(UserService.class);
        when(service.update(eq(1L), any())).thenReturn(
                UserDto.builder().id(1L).username("Updated").email("u@mail.com").age(40).build()
        );

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();

        mvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "Updated", "email": "u@mail.com", "age": 40}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Updated"));

        verify(service).update(eq(1L), any());
    }

    @Test
    void testDelete() throws Exception {
        UserService service = mock(UserService.class);

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service)).build();

        mvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
