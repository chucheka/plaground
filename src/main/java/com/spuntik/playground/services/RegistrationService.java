package com.spuntik.playground.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spuntik.playground.dao.UserRepository;
import com.spuntik.playground.entities.User;
import com.spuntik.playground.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    public User registerUser(UserDto userDto){

        ObjectMapper mapper = new ObjectMapper();

        User user =  mapper.convertValue(userDto,User.class);

        log.info("THE USER DTO OBJECT {}", user);


        return userRepository.save(user);
    }

    public List<User> getUsers() {

        return userRepository.findAll();
    }
}
