package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API для управления пользователями")
public class UserController {

    private final UserService service;

    /**
     * GET /api/users - Получить всех пользователей
     */
    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен успешно",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    public ResponseEntity<CollectionModel<UserDto>> getAll() {
        List<UserDto> users = service.findAll();

        // Добавляем HATEOAS ссылки к каждому пользователю
        users.forEach(user -> user.add(
                linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel()
        ));

        // Создаем CollectionModel с HATEOAS ссылками
        CollectionModel<UserDto> collectionModel = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users")
        );

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * GET /api/users/{id} - Получить пользователя по ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя с указанным ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "500", description = "Пользователь не найден")
    })
    public ResponseEntity<UserDto> getById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        UserDto user = service.findById(id);

        // Добавляем HATEOAS ссылки
        user.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));
        user.add(linkTo(methodOn(UserController.class).update(id, null)).withRel("update"));
        user.add(linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/users - Создать нового пользователя
     */
    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    public ResponseEntity<UserDto> create(
            @Parameter(description = "Данные пользователя")
            @RequestBody UserDto dto) {
        UserDto createdUser = service.create(dto);

        // Добавляем HATEOAS ссылки
        createdUser.add(linkTo(methodOn(UserController.class).getById(createdUser.getId())).withSelfRel());
        createdUser.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * PUT /api/users/{id} - Обновить пользователя
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет существующего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    public ResponseEntity<UserDto> update(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя")
            @RequestBody UserDto dto) {
        UserDto updatedUser = service.update(id, dto);

        // Добавляем HATEOAS ссылки
        updatedUser.add(linkTo(methodOn(UserController.class).getById(id)).withSelfRel());
        updatedUser.add(linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));
        updatedUser.add(linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id} - Удалить пользователя
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя с указанным ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
