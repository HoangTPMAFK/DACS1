package com.entity.demo;

import java.math.BigInteger;

import jakarta.persistence.*;

@Entity
@Table(name = "Accounts")
public class Account {
    @Version
    private BigInteger version;
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column(name = "userId")
    private String username;
    @Column(name = "userPassword")
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public Account() {
    }
    @Override
    public String toString() {
        return "Account [username=" + username + ", password=" + password + "]";
    }
}
