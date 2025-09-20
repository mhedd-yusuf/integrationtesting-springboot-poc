package com.example.integrationtestingspringbootpoc.web.it;

import com.example.integrationtestingspringbootpoc.domain.model.Transaction;
import com.example.integrationtestingspringbootpoc.utility.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class use real postgres DB to make sure the application
 * run as expected in the prod env
 */
@IntegrationTest(profiles = "postgres")
@Testcontainers
class TransactionControllerIT2 {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    // 1) Start a real Postgres just for the tests
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("transactionsdb")
                    .withUsername("test")
                    .withPassword("test");
    // .withReuse(true) // optional speed-up locally (enable reuse globally)

    // 2) Wire Spring to use the container’s JDBC settings
    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        // let Hibernate create the schema for the tests
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // if you have any data.sql that should run AFTER JPA creates tables
        r.add("spring.jpa.defer-datasource-initialization", () -> "true");
        // Optional if you want to be explicit:
        r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        // If you DO NOT want Boot to run data.sql:
        // r.add("spring.sql.init.mode", () -> "never");
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void verify_createTransactions() throws Exception {
        Transaction actual = JsonHelper.jsonAs("/json/request/transaction_create_valid.json", Transaction.class, objectMapper);
        MvcResult mvcResult = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.json("/json/request/transaction_create_valid.json")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        Transaction expected = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Transaction>() {
                });
        // verify the created transaction
        assertNotNull(expected);
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getAmount(), expected.getAmount());
        // verify the created transaction stored in the db by hitting the location attribute
        String asString = mockMvc.perform(get(mvcResult.getResponse().getHeader("Location")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

    }

    @Test
    void verify_getTransactions() throws Exception {
        List<Transaction> actual = JsonHelper.jsonAs("/json/response/list_transactions.json",
                new TypeReference<List<Transaction>>() {
                }
                , objectMapper);
        MvcResult mvcResult = mockMvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.json("/json/response/list_transactions.json")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<Transaction> expected = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Transaction>>() {
                });
        assertNotNull(expected);
        assertEquals(actual.size(), expected.size());
    }

    @Test
    void verify_getTransactions_pagination() throws Exception {
        List<Transaction> actual = JsonHelper.jsonAs("/json/response/list_transactions.json",
                new TypeReference<List<Transaction>>() {
                }
                , objectMapper);
        MvcResult mvcResult = mockMvc.perform(get("/transactions?page=0&size=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.json("/json/response/list_transactions.json")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<Transaction> expected = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Transaction>>() {
                });
        assertNotNull(expected);
        assertEquals(5, expected.size());
    }
    // Testing bad request
    /*mockMvc.perform(post("/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"currency\":\"USD\"}")) // missing amount
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("amount"));*/

    // Test not found
   /* mockMvc.perform(get("/transactions/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Transaction not found"));*/
    // Test conflict
    /*mockMvc.perform(post("/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(Fixtures.json("json/requests/transaction-duplicate.json")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.errorCode").value("TXN_DUPLICATE"));*/
    // Testing sequrity
   /* mockMvc.perform(get("/transactions").with(user("bob").roles("USER")))
            .andExpect(status().isOk());

mockMvc.perform(get("/transactions").with(user("bob").roles("GUEST")))
            .andExpect(status().isForbidden());*/


}