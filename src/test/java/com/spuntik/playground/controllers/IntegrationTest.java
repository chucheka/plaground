//package com.spuntik.playground.controllers;
//
//import eu.rekawek.toxiproxy.Proxy;
//import eu.rekawek.toxiproxy.ToxiproxyClient;
//import eu.rekawek.toxiproxy.model.ToxicDirection;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Rule;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.reactive.ClientHttpConnector;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.client.ResourceAccessException;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.testcontainers.containers.MSSQLServerContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.ToxiproxyContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import reactor.netty.http.client.HttpClient;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.SocketTimeoutException;
//import java.time.Duration;
//import java.time.temporal.ChronoUnit;
//import java.util.concurrent.TimeoutException;
//
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.springframework.test.util.AssertionErrors.assertTrue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Disabled
//@Slf4j
//@Testcontainers
//@AutoConfigureWebTestClient
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class IntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    private static Integer proxyPort = 20001;
//
//
//    @Rule
//    @Container
//    static ToxiproxyContainer toxiproxy;
//    @Rule
//    @Container
//    static MSSQLServerContainer<?> mssqlserver;
//
//    static {
//
//        toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
//                .withNetworkMode("bridge")
//                .withExposedPorts(8474, proxyPort);
//
//        mssqlserver = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
//                .acceptLicense()
//                .withExposedPorts(1433)
////                .withNetwork(network)
//                .withInitScript("init.sql");
//
//    }
//
////    @TestConfiguration
////    static class TestConfig {
////
////        @Bean
////        public WebClient webClient() {
////            HttpClient httpClient = HttpClient.create()
////                    .responseTimeout(Duration.ofSeconds(5)); // set read timeout to 5 seconds
////            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
////            return WebClient.builder().clientConnector(connector).build();
////        }
////
////        @Bean
////        public WebTestClient webTestClient(WebClient webClient) {
////            return WebTestClient.bindToServer((ClientHttpConnector) webClient).build();
////        }
////    }
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Test
//    @Disabled
//    public void proxyOnlyWorks() throws Exception {
//        ToxiproxyClient toxiproxyClient =
//                new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getFirstMappedPort());
//
//        InetAddress hostRunningTheAppEndpoint = InetAddress.getLocalHost();
//
//
//        Proxy proxy =
//                toxiproxyClient.createProxy(
//                        "my-proxy", "0.0.0.0:" + proxyPort, hostRunningTheAppEndpoint.getHostAddress() + ":" + port);
//
//        proxy.toxics().latency("latency", ToxicDirection.UPSTREAM, 8_000);
//
//        proxy.enable();
////
//
//        ResourceAccessException resourceAccessException =
//                assertThrows(
//                        ResourceAccessException.class,
//                        () -> this.webTestClient.mutate() // create a new instance with custom timeout
//                                .responseTimeout(Duration.ofSeconds(5))
//                                .build()
//                                .get()
//                                .uri("http://localhost:" + toxiproxy.getMappedPort(proxyPort) + "/api/v1/ping")
//                                .accept(MediaType.TEXT_PLAIN)
//                                .exchange()
//                                .expectStatus().isOk()
//                                .expectBody(String.class).isEqualTo("Alive"));
//
//        // Assert that the response took at least 5 seconds to arrive (due to the latency toxic)
////        assertTrue(response.getHeaders().containsKey("X-Toxic-Latency-Milliseconds"));
////        assertTrue(Integer.parseInt(response.getHeaders().getFirst("X-Toxic-Latency-Milliseconds")) >= 5000);
//        System.out.println("THE EXCEPTION MESSAGE " + resourceAccessException.getMessage());
//        assertThat(resourceAccessException.getRootCause().getClass(), equalTo(SocketTimeoutException.class));
//
//
//    }
//
//    @Test
//    public void proxyOnlyWorks2() throws Exception {
//        ToxiproxyClient toxiproxyClient =
//                new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getFirstMappedPort());
//
//        InetAddress hostRunningTheAppEndpoint = InetAddress.getLocalHost();
//
//
//        Proxy proxy =
//                toxiproxyClient.createProxy(
//                        "my-proxy", "0.0.0.0:" + proxyPort, hostRunningTheAppEndpoint.getHostAddress() + ":" + port);
//
//        proxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 6_000);
//
//
//        Duration timeoutDuration = Duration.ofSeconds(8);
//        String pingUri = "http://localhost:" + toxiproxy.getMappedPort(proxyPort) + "/api/v1/ping";
//
//        this.webTestClient.mutate() // create a new instance with custom timeout
//                .responseTimeout(timeoutDuration)
//                .build()
//                .get()
//                .uri(pingUri)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .consumeWith(response -> {
//                    assertThat(response.getResponseBody(), equalTo("Alive"));
//                });
////                .expectErrorMatches(throwable -> throwable instanceof TimeoutException)
////                .verify(timeoutDuration);
//    }
//
//
////    @Test
////    @Disabled
////    public void proxyWithLatencyTimeout() throws IOException {
////        ToxiproxyClient client =
////                new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getFirstMappedPort());
////
////        InetAddress hostRunningTheAppEndpoint = InetAddress.getLocalHost();
////
////        Proxy proxy =
////                client.createProxy(
////                        "my-proxy", "0.0.0.0:" + proxyPort, hostRunningTheAppEndpoint.getHostAddress() + ":" + port);
////
////        proxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 6_000);
////
////        ResourceAccessException resourceAccessException =
////                assertThrows(
////                        ResourceAccessException.class,
////                        () -> this.restTemplate.getForEntity(
////                                        "http://localhost:" + toxiproxy.getMappedPort(proxyPort) + "/api/v1/ping", String.class));
////
////        // Assert that the response took at least 5 seconds to arrive (due to the latency toxic)
//////        assertTrue(response.getHeaders().containsKey("X-Toxic-Latency-Milliseconds"));
//////        assertTrue(Integer.parseInt(response.getHeaders().getFirst("X-Toxic-Latency-Milliseconds")) >= 5000);
////        System.out.println("THE EXCEPTION MESSAGE " + resourceAccessException.getMessage());
////        assertThat(resourceAccessException.getRootCause().getClass(), equalTo(SocketTimeoutException.class));
////
////    }
//}
