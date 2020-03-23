package com.erokhina.dao;

import java.util.concurrent.Executors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import com.erokhina.helpers.MongoDBHelper;
import com.erokhina.model.Product;
import com.erokhina.model.User;
import com.mongodb.rx.client.MongoDatabase;
import org.bson.Document;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CatalogDao {
    private final int NUM_OF_THREADS = 6;
    private final Func1<Document, Product> productMapper = x ->
            new Product(
                    x.getString("id"),
                    x.getString("title"),
                    x.getString("price"),
                    x.getString("currency"));

    private Scheduler scheduler;
    private MongoDBHelper helper;

    public CatalogDao(MongoDatabase database) {
        this.helper = new MongoDBHelper(database);
        scheduler = Schedulers.from(Executors.newFixedThreadPool(NUM_OF_THREADS));
    }

    public Observable<Boolean> userAdd(User user) {
        return userById(user.getId())
                .flatMap(x -> {
                    if (x != null) return Observable.just(false);
                    else return helper.insertOneTo("user", user.toDocument());
                }).subscribeOn(scheduler);
    }

    public Observable<User> userById(String userId) {
        return helper.findById("user", userId)
                .map(x ->
                        new User(
                                x.getString("id"),
                                x.getString("name"),
                                x.getString("login"),
                                x.getString("currency"))
                ).singleOrDefault(null).subscribeOn(scheduler);
    }

    public Observable<Boolean> productAdd(Product product) {
        return helper.insertOneTo("product", product.toDocument());
    }

    public Observable<Product> productById(String userId, String productId) {
        return userById(userId).flatMap(user -> helper.findById("product", productId).singleOrDefault(null)
                .map(productMapper)
                .map(product -> new Product(
                        product.getId(),
                        product.getTitle(),
                        Integer.toString((int)Math.round(convertCurrency(product.getCurrency(), user.getCurrency(),
                                Double.parseDouble(product.getPrice())))),
                        user.getCurrency())).subscribeOn(scheduler));
    }

    public Observable<Product> productsGet(String userId, int limit, int offset) {
        return userById(userId).flatMap(user -> helper.getAll("product")
                .map(productMapper).skip(offset).take(limit)
                .map(product -> new Product(
                product.getId(),
                product.getTitle(),
                Double.toString(convertCurrency(product.getCurrency(), user.getCurrency(),
                        Double.parseDouble(product.getPrice()))),
                user.getCurrency())).subscribeOn(scheduler));
    }

    private double convertCurrency(String from, String to, double val) {
        MonetaryAmount amount = Monetary.getDefaultAmountFactory().setCurrency(from)
                .setNumber(val).create();

        CurrencyConversion conversion = MonetaryConversions.getConversion(to);

        MonetaryAmount converted = amount.with(conversion);
        return converted.getNumber().doubleValueExact();
    }
}
