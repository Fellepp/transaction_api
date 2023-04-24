package com.bank.transaction_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long registeredTime;
    private long executedTime;
    private boolean success;
    private String successDescription;
    private double cashAmount;
    @OneToOne
    private Account sourceAccount;
    @OneToOne
    private Account destinationAccount;

    public Transaction(double cashAmount, Account sourceAccount, Account destinationAccount, long registeredTime, boolean success) {
        this.cashAmount = cashAmount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.registeredTime = registeredTime;
        this.success = success;
        if (success) {
            this.executedTime = System.currentTimeMillis();
        } else {
            this.executedTime = -1;
        }
    }

    public Transaction() {
    }

    public long getId() {
        return id;
    }

    public long getRegisteredTime() {
        return registeredTime;
    }

    public long getExecutedTime() {
        return executedTime;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getSuccessDescription() {
        return successDescription;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setSuccessDescription(String description) {
        this.successDescription = description;
    }

    public String toString() {
        try{
            return "\nTransaction ID: " + id + "\nSource Account: " + sourceAccount.toString() + "\nDestination Account: " + destinationAccount.toString()
                    + "\nRegistered Time: " + registeredTime + "\nExecuted Time " + executedTime + "\nTransaction status: " + success;
        }catch(Exception e){
            return null;
        }
    }
}
