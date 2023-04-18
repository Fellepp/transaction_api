package com.bank.transaction_api.service;

import com.bank.transaction_api.model.Account;
import com.bank.transaction_api.model.Transaction;
import com.bank.transaction_api.repository.AccountRepository;
import com.bank.transaction_api.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
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
        return transactionRepo.findValidTransactions();
    }

    @Transactional
    public ResponseEntity<?> transaction(long registeredTime, double cashAmount, long sourceAccountId, long destinationAccountId) {
        Account sourceAccount;
        Account destinationAccount;

        Optional<Account> optionalSourceAccount = accountRepo.findById(sourceAccountId);
        Optional<Account> optionalDestinationAccount = accountRepo.findById(destinationAccountId);

        try {
            sourceAccount = optionalSourceAccount.get();
            destinationAccount = optionalDestinationAccount.get();
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
        } else if (withdraw(sourceAccount, cashAmount) &&
                deposit(destinationAccount, cashAmount)) {

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

    private boolean withdraw(Account sourceAccount, double cashAmount) {
        if (cashAmount < 0) {
            System.out.println("The cash amount cant be negative.");
            return false;
        } else if (sourceAccount.getAvailableCash() - cashAmount < 0) {
            System.out.println("The cash amount is too high. Negative values in bank is not allowed.");
            return false;
        }
        sourceAccount.setAvailableCash(sourceAccount.getAvailableCash() - cashAmount);
        return true;
    }

    private boolean deposit(Account destinationAccount, double cashAmount) {
        if (cashAmount < 0) {
            System.out.println("The cash amount cant be negative.");
            return false;
        }
        destinationAccount.setAvailableCash(destinationAccount.getAvailableCash() + cashAmount);
        return true;
    }
}
