package com.spuntik.playground.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spuntik.playground.dao.UserRepository;
import com.spuntik.playground.entities.User;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;
    @Rule
    public static Network network = Network.newNetwork();

    @Rule
    @Container
    static ToxiproxyContainer toxiproxy;
    @Rule
    @Container
    static MSSQLServerContainer<?> mssqlserver;
    static ToxiproxyContainer.ContainerProxy dbProxy;

    static {

        toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
//                .withExposedPorts(8474)
                .withNetwork(network);

        mssqlserver = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
                .acceptLicense()
//                .withExposedPorts(1433)
                .withNetwork(network)
                .withInitScript("init.sql");

    }

    //
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {


//        registry.add("spring.datasource.url", () -> "jdbc:sqlserver://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(1433));
        registry.add("spring.datasource.url", () -> createDBProxy());
//        registry.add("spring.datasource.url", mssqlserver::getJdbcUrl);
        registry.add("spring.datasource.username", mssqlserver::getUsername);
        registry.add("spring.datasource.password", mssqlserver::getPassword);
//        registry.add("spring.datasource.driver-class-name", () -> SQL_SERVER_DRIVER_CLASS_NAME);
    }

    private static String createDBProxy() {
        dbProxy = toxiproxy.getProxy(mssqlserver, 1433);
        return "jdbc:sqlserver://" + dbProxy.getContainerIpAddress() + ":" + dbProxy.getProxyPort() + ";encrypt=false;trustServerCertificate=true";
    }

    @Test
    void testGetAllUsers() {

        log.info("THE BD CONFIG {}", mssqlserver.getJdbcUrl());
        log.info("THE BD CONFIG {}", dbProxy.getOriginalProxyPort());

        // Retrieve all users from the database
        List<User> users = userRepository.findAll();

        log.info("**************THE USERS ******* /n {}", users);
        // Verify the results
        assertEquals(2, users.size());
//        assertEquals("kxke", users.get(0).getUsername());
//        assertEquals("john.doe@example.com", users.get(0).getEmail());

    }

    @Test
    void should_be_able_to_save_one_user() throws Exception {
        // Given
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setAge(12);
        user.setEmail("ryanucheka@gmail.com");
        user.setPassword("password");
        user.setUsername("chike22");
        user.setLastName("Ucheka");
        user.setFirstName("Wilson");

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("chike22"))
                .andExpect(jsonPath("$.lastName").value("Ucheka"));
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void checkTimeoutConditionWithDatabase() throws IOException {

//        assertThat(userRepository.findByUsername("kxke")).isEmpty();

        dbProxy.toxics()
                .timeout("timeout", ToxicDirection.DOWNSTREAM, 1000);

        assertThatThrownBy(() -> userRepository.findAll())
                .isInstanceOf(Exception.class);

    }
//    @Test
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
//    void checkTimeoutConditionWithDatabase() throws IOException {
//
//        dbProxy.toxics().bandwidth("CUT_CONNECTION_DOWNSTREAM", ToxicDirection.DOWNSTREAM, 0);
//        dbProxy.toxics().bandwidth("CUT_CONNECTION_UPSTREAM", ToxicDirection.UPSTREAM, 0);
//
//        List<User> users = userRepository.findAll();
//
//        log.info("**************THE USERS ******* /n {}", users);
//        // Verify the results
//        assertEquals(2, users.size());
//
//    }


}
