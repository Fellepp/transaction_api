package com.example.transaction_api.controller;

import com.example.transaction_api.model.Account;
import com.example.transaction_api.model.Transaction;
import com.example.transaction_api.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("api/v1/bank")
@RestController
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/account/list")
    public List<Account> getAllAccounts() {
        return transactionService.getAllAccounts();
    }

    @GetMapping("/transaction/listAll")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/transaction/listValid")
    public List<Transaction> getValidTransactions() {
        return transactionService.getValidTransactions();
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> transaction(@RequestParam("cashAmount") double cashAmount,
                                         @RequestParam("sourceAccountId") long sourceAccountId,
                                         @RequestParam("destinationAccountId") long destinationAccountId) {
        return transactionService.transaction(System.currentTimeMillis(), cashAmount, sourceAccountId, destinationAccountId);
    }

}
