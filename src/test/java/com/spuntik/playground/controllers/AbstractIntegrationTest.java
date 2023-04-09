//package com.spuntik.playground.controllers;
//
//import com.spuntik.playground.dao.UserRepository;
//import eu.rekawek.toxiproxy.Proxy;
//import eu.rekawek.toxiproxy.ToxiproxyClient;
//import eu.rekawek.toxiproxy.model.ToxicDirection;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Rule;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MSSQLServerContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.ToxiproxyContainer;
//import org.testcontainers.containers.wait.strategy.Wait;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.io.IOException;
//
//
//
//public abstract class AbstractIntegrationTest {
//
////
////    @Rule
////    public static Network network = Network.newNetwork();
//    private static final String SQL_SERVER_USERNAME = "sa";
//    private static final String SQL_SERVER_PASSWORD = "Test@123";
//    private static final String SQL_SERVER_DATABASE = "playground";
//    private static final String SQL_SERVER_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//
//
//
//
////    @Container
////    final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
//
////        final ToxiproxyClient toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
////        final Proxy proxy = toxiproxyClient.createProxy("sqlserver", "0.0.0.0:13306", "sqlserver:1433");
//
////        System.out.println("THE MAPPED PORT " + toxiproxy.getMappedPort(13306));
//
////        System.out.println("THE DATABASE NAME "+mssqlserver.getDatabaseName());
//
////        registry.add("spring.datasource.url",() -> "jdbc:sqlserver://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(13306) + ";databaseName=" + SQL_SERVER_DATABASE);
//        registry.add("spring.datasource.url", mssqlserver::getJdbcUrl);
//        registry.add("spring.datasource.username", mssqlserver::getUsername);
//        registry.add("spring.datasource.password", mssqlserver::getPassword);
////        registry.add("spring.datasource.driver-class-name", () -> SQL_SERVER_DRIVER_CLASS_NAME);
//    }
//}
