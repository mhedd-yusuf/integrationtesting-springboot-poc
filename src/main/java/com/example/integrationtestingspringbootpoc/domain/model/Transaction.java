package com.example.integrationtestingspringbootpoc.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    private UUID id;

    @Column(name = "credit_card_number")
    private String creditCardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private TransactionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    @Column
    private BigDecimal amount;

}