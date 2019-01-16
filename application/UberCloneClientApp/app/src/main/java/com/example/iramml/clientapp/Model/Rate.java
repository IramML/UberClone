package com.example.iramml.clientapp.Model;

public class Rate {
    private String rates, comment;

    public Rate() {
    }

    public Rate(String rates, String comment) {
        this.rates = rates;
        this.comment = comment;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
