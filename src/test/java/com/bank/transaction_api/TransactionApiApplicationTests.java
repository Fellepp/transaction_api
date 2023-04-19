package com.bank.transaction_api;

import com.bank.transaction_api.model.Account;
import com.bank.transaction_api.model.Transaction;
import com.bank.transaction_api.repository.AccountRepository;
import com.bank.transaction_api.repository.TransactionRepository;
import com.bank.transaction_api.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {TransactionApiApplication.class})
public class TransactionApiApplicationTests {

    @Mock
    private AccountRepository accountRepo;
    @Mock
    private TransactionRepository transactionRepo;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destinationAccount;

    @Before
    public void setUp() {
        // Create mock accounts
        sourceAccount = new Account(1L, "Hansel", 100.00);
        destinationAccount = new Account(2L, "Gretel", 200.00);

        // Set up mock repository methods
        when(accountRepo.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));
        when(accountRepo.save(any(Account.class))).thenReturn(sourceAccount, destinationAccount);
    }

    @Test
    public void testTransactionValidInput() {
        // Call transaction method
        ResponseEntity<?> response = transactionService.transaction(System.currentTimeMillis(), 50, sourceAccount.getId(), destinationAccount.getId());

        // Check response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that accounts are updated
        assertEquals(50.0, sourceAccount.getAvailableCash(), 0.01);
        assertEquals(250.0, destinationAccount.getAvailableCash(), 0.01);

        // Verify that the transaction was saved
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    public void testTransactionNegativeCashAmount() {
        // Call transaction method with negative cash amount
        ResponseEntity<?> response = transactionService.transaction(System.currentTimeMillis(), -50, sourceAccount.getId(), destinationAccount.getId());

        // Check response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Negative values are not supported", response.getBody());

        // Verify that accounts were not updated
        assertEquals(100.0, sourceAccount.getAvailableCash(), 0.01);
        assertEquals(200.0, destinationAccount.getAvailableCash(), 0.01);

        // Verify that the transaction was saved
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    public void testTransactionInvalidAccountId() {
        // Call transaction method with invalid account ID
        ResponseEntity<?> response = transactionService.transaction(System.currentTimeMillis(), 50, -1, destinationAccount.getId());

        // Check response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("One of the accounts could not be found.", response.getBody());

        // Verify that accounts were not updated
        assertEquals(100.0, sourceAccount.getAvailableCash(), 0.01);
        assertEquals(200.0, destinationAccount.getAvailableCash(), 0.01);

        // Verify that the transaction was saved
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    public void testTransactionCashAmountTooHigh() {
        // Call transaction method with cash amount too high
        ResponseEntity<?> response = transactionService.transaction(System.currentTimeMillis(), 5000, sourceAccount.getId(), destinationAccount.getId());

        // Check response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Negative values are not supported", response.getBody());

        // Verify that accounts were not updated
        assertEquals(100.0, sourceAccount.getAvailableCash(), 0.01);
        assertEquals(200.0, destinationAccount.getAvailableCash(), 0.01);

        // Verify that the transaction was saved
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    public void testTransactionSourceAndDestinationIsEqual() {
        // Call transaction method with source account and destination account being the same
        ResponseEntity<?> response = transactionService.transaction(System.currentTimeMillis(), 50, sourceAccount.getId(), sourceAccount.getId());

        // Check response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot use the same account for source and destination.", response.getBody());

        // Verify that accounts were not updated
        assertEquals(100.0, sourceAccount.getAvailableCash(), 0.01);
        assertEquals(200.0, destinationAccount.getAvailableCash(), 0.01);

        // Verify that the transaction was saved
        verify(transactionRepo).save(any(Transaction.class));
    }
}
