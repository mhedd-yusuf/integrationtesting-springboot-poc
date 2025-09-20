package com.example.integrationtestingspringbootpoc.web;

import com.example.integrationtestingspringbootpoc.domain.model.Transaction;
import com.example.integrationtestingspringbootpoc.service.TransactionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionQueryService transactionQueryService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@PageableDefault(
            size = 30,
            direction = Sort.Direction.ASC)
                                                             @SortDefault.SortDefaults(
                                                                     @SortDefault(sort = "dateTime",
                                                                             direction = Sort.Direction.ASC)
                                                             ) Pageable pageable) {
        return ResponseEntity.ok(transactionQueryService.findTransactions(pageable));
    }

    @GetMapping("{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionQueryService.findTransaction(id));
    }

    @GetMapping("/by-card/{creditCardNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsByCreditCardNumber(@PathVariable String creditCardNumber,
                                                                               @PageableDefault(
                                                                                       size = 30,
                                                                                       direction = Sort.Direction.ASC)
                                                                               @SortDefault.SortDefaults(
                                                                                       @SortDefault(sort = "dateTime",
                                                                                               direction = Sort.Direction.ASC)
                                                                               ) Pageable pageable) {
        return ResponseEntity.ok(transactionQueryService.findTransactionsByCreditCardNumber(creditCardNumber, pageable));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transactionRequest) {
        Transaction transaction = transactionQueryService.createTransaction(transactionRequest);
        return ResponseEntity.created(URI.create("/transactions/" + transaction.getId()))
                .body(transaction);
    }

}
