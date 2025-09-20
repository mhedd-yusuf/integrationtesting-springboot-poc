package com.example.integrationtestingspringbootpoc.web;

import com.example.integrationtestingspringbootpoc.domain.model.Transaction;
import com.example.integrationtestingspringbootpoc.service.TransactionQueryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * In this test we are loading only the web layer and mocking the service layer
 * The purpose is to verify only the controller layer
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TransactionQueryService transactionQueryService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void verify_getTransactions() throws Exception {
        List<UUID> uuids = List.of(UUID.randomUUID(),
                UUID.randomUUID());
        when(transactionQueryService.findTransactions(any())).thenReturn(List.of(
                Transaction
                        .builder()
                        .id(uuids.get(0))
                        .build(),
                Transaction
                        .builder()
                        .id(uuids.get(0))
                        .build()
        ));
        MvcResult mvcResult = mockMvc.perform(get("/transactions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<Transaction> transactions = new ObjectMapper().readValue(mvcResult
                        .getResponse()
                        .getContentAsString(),
                new TypeReference<List<Transaction>>() {
                });
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
    }

}