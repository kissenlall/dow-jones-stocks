package com.coding.challenge;

import com.coding.challenge.dto.Stock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;

@ContextConfiguration(initializers = StockApplicationTests.TestEnvInitializer.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
class StockApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Container
    public static GenericContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("demo-test")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("init_script.sql");

    private WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToApplicationContext(this.context)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    @Test
    public void testUpload() throws IOException {

        client
                .post()
                .uri("/api/v1/stocks")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes()))
                .bodyValue(generateBody())
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    public void testAddNewRecord() throws IOException {

        Stock stock = new Stock();
        stock.setStock("TEST2");
        stock.setDate("1/7/2011");
        stock.setQuarter("1");

        client
                .post()
                .uri("/api/v1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(stock), Stock.class)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.ticker").isEqualTo("TEST2");
    }

    @Test
    public void testGetExistingStock() throws IOException {

        client
                .get()
                .uri("/api/v1/stock/TEST")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectBody().jsonPath("$[0].ticker").isEqualTo("TEST");
    }

    @Test
    public void testGetNonExistingStock() throws IOException {

        client
                .get()
                .uri("/api/v1/stock/TEST1")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes()))
                .exchange()
                .expectBody().jsonPath("$").isEmpty();
    }

    private MultiValueMap<String, HttpEntity<?>> generateBody() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("/dow_jones_index.csv", StockApplicationTests.class));
        return builder.build();
    }

    public static class TestEnvInitializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.host=localhost");
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.username=" + ((PostgreSQLContainer) postgreSQLContainer).getUsername());
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.password=" + ((PostgreSQLContainer) postgreSQLContainer).getPassword());
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.name=" + ((PostgreSQLContainer) postgreSQLContainer).getDatabaseName());
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.port=" + ((PostgreSQLContainer) postgreSQLContainer).getFirstMappedPort());
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.pool.initialSize=2");
            addInlinedPropertiesToEnvironment(configurableApplicationContext, "spring.r2dbc.pool.maxSize=10");

        }
    }
}
