package com.example.transaction_api.model;

import jakarta.persistence.*;

@Entity
@Table(name="Account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private double availableCash;

    public Account(long id, String name, double availableCash) {
        this.id = id;
        this.name = name;
        this.availableCash = availableCash;
    }

    public Account() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAvailableCash() {
        return availableCash;
    }

    public boolean withdraw(double cashAmount) {
        if (cashAmount < 0) {
            System.out.println("The cash amount cant be negative.");
            return false;
        }
        if (availableCash - cashAmount < 0) {
            System.out.println("The cash amount is too high. Negative values in bank is not allowed.");
            return false;
        }
        availableCash -= cashAmount;
        return true;
    }
    public boolean deposit(double cashAmount) {
        if (cashAmount < 0) {
            System.out.println("The cash amount cant be negative.");
            return false;
        }
        availableCash += cashAmount;
        return true;
    }

    public String toString(){
        return "Bank ID: " + id + ", Name: " + name + ", Balance: " + availableCash;
    }
}
