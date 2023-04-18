package com.bank.transaction_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Account")
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

    public void setAvailableCash(double newAvailableCash) {
        availableCash = newAvailableCash;
    }

    public String toString() {
        return "Bank ID: " + id + ", Name: " + name + ", Balance: " + availableCash;
    }
}
