package com.spuntik.playground.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)

    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;

    private Integer age;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

}
