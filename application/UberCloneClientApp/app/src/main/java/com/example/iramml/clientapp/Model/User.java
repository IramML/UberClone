package com.example.iramml.clientapp.Model;

public class User {
    private String email,password,name,phone, avatarUrl, rates;

    public User(){

    }

    public User(String email, String password, String name, String phone, String avatarUrl, String rates) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.rates = rates;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }
}
