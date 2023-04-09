package com.spuntik.playground.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserDto implements Serializable {
    private String username;
    private String password;
    private String email;
    private Integer age;
    private String firstName;
    private String lastName;
}
