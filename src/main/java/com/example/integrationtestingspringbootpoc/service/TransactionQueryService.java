package com.example.integrationtestingspringbootpoc.service;


import com.example.integrationtestingspringbootpoc.domain.model.Transaction;
import com.example.integrationtestingspringbootpoc.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public Transaction findTransaction(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .toList();
    }

    public List<Transaction> findTransactionsByCreditCardNumber(String creditCardNumber, Pageable pageable) {
        return transactionRepository.findAllByCreditCardNumber(creditCardNumber, pageable);
    }

    /**
     * In this example we are passing the entity just for simplicity, production code should use a DTO
     * to protect internal data
     *
     * @param transaction a request to be added to the DB
     * @return a created request
     */
    @Transactional(readOnly = false)
    public Transaction createTransaction(Transaction transaction) {
        try {
            return transactionRepository.save(transaction);
        }
        catch (Exception exception){
            System.out.println("Exception");
            throw  new RuntimeException();
        }
    }
}
