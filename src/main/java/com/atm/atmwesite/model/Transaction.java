package com.atm.atmwesite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions") 
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    
    public Transaction() {
    }

    
    public Transaction(double amount, User user, String type) {
        this.amount = amount;
        this.user = user;
        this.type = type;
    }

    // ---------- getters & setters ----------

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
