package com.spuntik.playground.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.spuntik.playground.dao.UserRepository;
import com.spuntik.playground.entities.User;
import com.spuntik.playground.model.Item;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaygroundIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    UserRepository userRepository;

    @Rule
    @Container
    static ToxiproxyContainer toxiproxy;
    @Rule
    @Container
    static MSSQLServerContainer<?> mssqlserver;
    static ToxiproxyContainer.ContainerProxy dbProxy;

    @Rule
    public static Network network = Network.newNetwork();

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort())
            .build();

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

    private static String createDBProxy() {
        dbProxy = toxiproxy.getProxy(mssqlserver, 1433);
        return "jdbc:sqlserver://" + dbProxy.getContainerIpAddress() + ":" + dbProxy.getProxyPort() + ";encrypt=false;trustServerCertificate=true";
    }
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("amazon.base.url", wireMockServer::baseUrl);
        registry.add("spring.datasource.url", () -> createDBProxy());
//        registry.add("spring.datasource.url", mssqlserver::getJdbcUrl);
        registry.add("spring.datasource.username", mssqlserver::getUsername);
        registry.add("spring.datasource.password", mssqlserver::getPassword);
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void checkTimeoutConditionWithDatabase() throws IOException {

//        assertThat(userRepository.findByUsername("kxke")).isEmpty();

        dbProxy.toxics()
                .timeout("timeout", ToxicDirection.DOWNSTREAM, 1000);

        assertThatThrownBy(() -> userRepository.findAll())
                .isInstanceOf(Exception.class);

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

        this.webTestClient
                .post()
                .uri("/api/v1/users")
                .body(Mono.just(user), User.class)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(CREATED)
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.username").isEqualTo("chike22")
                .jsonPath("$.lastName").isEqualTo("Ucheka");

    }

    @Test
    void should_be_able_to_create_item() throws JsonProcessingException {

//        WireMockRuntimeInfo wm1RuntimeInfo = wm1.getRuntimeInfo();

        Item item = new Item();

        item.setId(12L);
        item.setAmount(BigDecimal.valueOf(800.00));
        item.setName("Think and Grow Rich");
        item.setCategory("Book");
        item.setDescription("Napolian Hills book");


        wireMockServer.stubFor(WireMock.post("/amazon/item")
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(new ObjectMapper().writeValueAsString(item)))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
//                                .withFixedDelay(4000)
                                .withBody(new ObjectMapper().writeValueAsString(item))
//                        .withBasicAuth("username", "plain-password")
                )


        );


        this.webTestClient
                .post()
                .uri("/item")
                .body(Mono.just(item), Item.class)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(CREATED)
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Item created successfully")
                .jsonPath("$.status").isEqualTo("SUCCESSFUL")
                .jsonPath("$.data.id").isEqualTo(12L)
                .jsonPath("$.data.name").isEqualTo("Think and Grow Rich");
    }

    @Test
    void should_be_able_to_get_item() {

//        WireMockRuntimeInfo wm1RuntimeInfo = wm1.getRuntimeInfo();

        wireMockServer.stubFor(get("/amazon/item")
                .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
//                                .withFixedDelay(4000)
                                .withBodyFile("item.json")
//                        .withBasicAuth("username", "plain-password")
                ));

        this.webTestClient
                .get()
                .uri("/item")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Book purchase successful")
                .jsonPath("$.status").isEqualTo("SUCCESSFUL")
                .jsonPath("$.data.id").isEqualTo(11)
                .jsonPath("$.data.name").isEqualTo("Richest Man In Babylon");
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
