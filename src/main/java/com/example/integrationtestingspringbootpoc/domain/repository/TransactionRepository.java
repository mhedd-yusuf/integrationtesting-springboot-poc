package com.example.integrationtestingspringbootpoc.domain.repository;

import com.example.integrationtestingspringbootpoc.domain.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Get all transactions by a credit card number, order by passed parameters
     *
     * @return list of transaction
     */
    List<Transaction> findAllByCreditCardNumber(String creditCardNumber, Pageable pageable);

    /**
     * Get a single transaction by ID
     *
     * @return a transaction
     */
    Transaction findById(UUID id);

}
