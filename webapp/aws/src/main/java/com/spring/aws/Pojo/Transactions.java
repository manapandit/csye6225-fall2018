package com.spring.aws.Pojo;

import com.spring.aws.Pojo.UserRegistration;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="transactions")
public class Transactions implements Serializable {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "transaction_id")
    @Column(unique = true, nullable = false)
    private String transactionId;

    @Column(name = "description")
    private String description;

    @Column(name = "merch")
    private String merchant;

    @Column(name = "category")
    private String category;

    @Column(name = "amount")
    private String amount;

    @Column(name = "date")
    private String date;

    @OneToOne(targetEntity = UserRegistration.class)
    private UserRegistration user;

    public UserRegistration getUser() {
        return user;
    }

    public void setUser(UserRegistration user) {
        this.user = user;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Transactions() {

    }
}
