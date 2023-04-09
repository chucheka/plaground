package com.spuntik.playground.controllers;

import com.spuntik.playground.entities.User;
import com.spuntik.playground.model.UserDto;
import com.spuntik.playground.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final RegistrationService registrationService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody UserDto userDto) {

        log.info("THE USER DTO {}", userDto);

        return registrationService.registerUser(userDto);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return registrationService.getUsers();
    }

    @GetMapping(value = "/ping",produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        log.info("******************** THE PING CALLED******************************");
        return "Alive";
    }
}
