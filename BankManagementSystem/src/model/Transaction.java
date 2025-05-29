package model;

import java.time.LocalDateTime; //timestamp er jonno java library

public class Transaction {
    private int id; //transaction er id
    private int accountId; //transaction je account id korlo tar id
    private String type; //transaction er type. Oi withdraw, naki deposit, transfer etc.
    private double amount;
    private LocalDateTime timestamp;

    //amader constructor
    public Transaction(int id, int accountId, String type, double amount, LocalDateTime timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    //getter, setter dite hobe vai.
}

