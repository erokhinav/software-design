package com.erokhina.model;

import java.util.Map;

import org.bson.Document;

public class User {
    public final String id;
    public final String name;
    public final String login;
    public final String currency;


    public User(Document doc) {
        this(
                doc.getString("id"),
                doc.getString("name"),
                doc.getString("login"),
                doc.getString("currency")
        );
    }

    public User(String  id, String name, String login, String currency) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }
    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    public Document toDocument() {
        return new Document(Map.of("id", id, "name", name, "login", login, "currency", currency));
    }
}
