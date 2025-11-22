package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserEvent;
import com.example.userservice.entity.User;
import com.example.userservice.kafka.UserEventProducer;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class UserServiceImplTest {

    @Test
    void testFindAll() {
        UserRepository repo = mock(UserRepository.class);
        UserEventProducer producer = mock(UserEventProducer.class);

        when(repo.findAll()).thenReturn(List.of(
                User.builder().id(1L).username("A").email("a@mail.com").age(20).build()
        ));

        UserServiceImpl service = new UserServiceImpl(repo, producer);

        List<UserDto> list = service.findAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getUsername()).isEqualTo("A");

        verify(repo).findAll();
    }

    @Test
    void testFindById() {
        UserRepository repo = mock(UserRepository.class);
        UserEventProducer producer = mock(UserEventProducer.class);

        when(repo.findById(1L)).thenReturn(Optional.of(
                User.builder().id(1L).username("B").email("b@mail.com").age(30).build()
        ));

        UserServiceImpl service = new UserServiceImpl(repo, producer);

        UserDto dto = service.findById(1L);

        assertThat(dto.getEmail()).isEqualTo("b@mail.com");
        verify(repo).findById(1L);
    }

    @Test
    void testCreate() {
        UserRepository repo = mock(UserRepository.class);
        UserEventProducer producer = mock(UserEventProducer.class);

        User user = User.builder()
                .id(1L)
                .username("C")
                .email("c@mail.com")
                .age(25)
                .build();

        when(repo.save(any())).thenReturn(user);

        UserServiceImpl service = new UserServiceImpl(repo, producer);

        UserDto dto = UserDto.builder()
                .username("C")
                .email("c@mail.com")
                .age(25)
                .build();

        UserDto result = service.create(dto);

        assertThat(result.getId()).isEqualTo(1);

        verify(repo).save(any());
        verify(producer).sendUserEvent(any(UserEvent.class));
    }

    @Test
    void testUpdate() {
        UserRepository repo = mock(UserRepository.class);
        UserEventProducer producer = mock(UserEventProducer.class);

        User existing = User.builder()
                .id(1L).username("Old").email("old@mail.com").age(50).build();

        User updated = User.builder()
                .id(1L).username("New").email("new@mail.com").age(30).build();

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(updated);

        UserServiceImpl service = new UserServiceImpl(repo, producer);

        UserDto dto = UserDto.builder()
                .username("New").email("new@mail.com").age(30).build();

        UserDto result = service.update(1L, dto);

        assertThat(result.getUsername()).isEqualTo("New");

        verify(repo).findById(1L);
        verify(repo).save(any());
    }

    @Test
    void testDelete() {
        UserRepository repo = mock(UserRepository.class);
        UserEventProducer producer = mock(UserEventProducer.class);

        User user = User.builder()
                .id(1L).username("A").email("a@mail.com").age(20).build();

        when(repo.findById(1L)).thenReturn(Optional.of(user));

        UserServiceImpl service = new UserServiceImpl(repo, producer);

        service.delete(1L);

        verify(repo).findById(1L);
        verify(repo).deleteById(1L);
        verify(producer).sendUserEvent(any(UserEvent.class));
    }
}
