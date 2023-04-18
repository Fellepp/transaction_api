package com.example.transaction_api.service;

import com.example.transaction_api.TranscationInterface.TransactionServiceInterface.TransactionServiceInterface;
import com.example.transaction_api.model.Account;
import com.example.transaction_api.model.Transaction;
import com.example.transaction_api.repository.AccountRepository;
import com.example.transaction_api.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceInterface {
    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;


    public TransactionService(AccountRepository accountRepo, TransactionRepository transactionRepo) {
        super();
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }

    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepo.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepo.findAll();
    }

    public List<Transaction> getValidTransactions() {
        return (List<Transaction>) transactionRepo.findValidTransactions();
    }

    public ResponseEntity<?> transaction(long registeredTime, double cashAmount, long sourceAccountId, long destinationAccountId) {
        Account sourceAccount;
        Account destinationAccount;

        Optional<Account> OptionalSourceAccount = accountRepo.findById(sourceAccountId);
        Optional<Account> OptionalDestinationAccount = accountRepo.findById(destinationAccountId);

        try {
            sourceAccount = OptionalSourceAccount.get();
            destinationAccount = OptionalDestinationAccount.get();
        } catch (Exception e) {
            Transaction returnTransaction = new Transaction(cashAmount, null, null, registeredTime, false);
            returnTransaction.setSuccessDescription("One of the accounts could not be found.");

            transactionRepo.save(returnTransaction);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One of the accounts could not be found.");
        }

        if (sourceAccountId == destinationAccountId) {
            Transaction returnTransaction = new Transaction(cashAmount, sourceAccount, destinationAccount, registeredTime, false);
            returnTransaction.setSuccessDescription("Cannot use the same account for source and destination");

            transactionRepo.save(returnTransaction);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot use the same account for source and destination.");
        }

        if (sourceAccount.withdraw(cashAmount) &&
                destinationAccount.deposit(cashAmount)) {

            accountRepo.save(sourceAccount);
            accountRepo.save(destinationAccount);

            Transaction returnTransaction = new Transaction(cashAmount, sourceAccount, destinationAccount, registeredTime, true);
            transactionRepo.save(returnTransaction);

            return ResponseEntity.status(HttpStatus.OK).body(returnTransaction.toString());
        } else {
            Transaction returnTransaction = new Transaction(cashAmount, sourceAccount, destinationAccount, registeredTime, false);
            returnTransaction.setSuccessDescription("Negative numbers are not allowed.");

            transactionRepo.save(returnTransaction);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Negative values are not supported");
        }
    }
}
