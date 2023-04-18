package com.example.transaction_api.repository;

import com.example.transaction_api.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("from Transaction where success = 1")
    public List<Transaction> findValidTransactions();
}
