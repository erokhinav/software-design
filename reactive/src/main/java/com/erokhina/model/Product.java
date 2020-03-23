package com.erokhina.model;

import java.util.Map;

import org.bson.Document;

public class Product {
    public final String id;
    public final String title;
    public final String price;
    public final String currency;


    public Product(Document doc) {
        this(
                doc.getString("id"),
                doc.getString("title"),
                doc.getString("price"),
                doc.getString("currency")
        );
    }

    public Product(String id, String title, String price, String currency) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getPrice() {
        return price;
    }
    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    public Document toDocument() {
        return new Document(Map.of("id", id, "title", title, "price", price, "currency", currency));
    }
}
