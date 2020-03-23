package com.erokhina;

import java.util.List;
import java.util.Map;

import com.erokhina.dao.CatalogDao;
import com.erokhina.handler.HttpHandler;
import com.erokhina.handler.HttpHandlerImpl;
import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.FindObservable;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.Success;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import rx.Observable;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

public class CatalogDaoTest {
    private HttpHandler handler;
    private MongoDatabase db;

    @Before
    public void setUp() {
        db = PowerMockito.mock(MongoDatabase.class);
        handler = new HttpHandlerImpl(new CatalogDao(db));
    }

    @Test
    public void testUserGet() {
        MongoCollection userCollection = PowerMockito.mock(MongoCollection.class);
        FindObservable findObservable = PowerMockito.mock(FindObservable.class);
        HttpServerRequest request = PowerMockito.mock(HttpServerRequest.class);

        when(db.getCollection("user")).thenReturn(userCollection);
        when(userCollection.find(Filters.eq("id", Mockito.anyString()))).thenReturn(findObservable);
        when(findObservable.toObservable())
                .thenReturn(Observable.just(new Document(
                        Map.of("id", "1",
                                "name", "tt",
                                "login", "rr",
                                "currency", "RUB"))));
        when(request.getDecodedPath()).thenReturn("/user/get");
        when(request.getQueryParameters()).thenReturn(Map.of("id", List.of("1")));

        handler.makeRequest(request).toBlocking().getIterator().forEachRemaining(x ->
                assertEquals(x, "User{id=1, name='tt', login='rr', currency='RUB'}"));
    }

    @Test
    public void testProductGet() {
        MongoCollection productCollection = PowerMockito.mock(MongoCollection.class);
        MongoCollection userCollection = PowerMockito.mock(MongoCollection.class);
        FindObservable findObservableProduct = PowerMockito.mock(FindObservable.class);
        FindObservable findObservableUser = PowerMockito.mock(FindObservable.class);
        HttpServerRequest request = PowerMockito.mock(HttpServerRequest.class);

        when(db.getCollection("product")).thenReturn(productCollection);
        when(db.getCollection("user")).thenReturn(userCollection);
        when(productCollection.find(Filters.eq("id", Mockito.anyString()))).thenReturn(findObservableProduct);
        when(userCollection.find(Filters.eq("id", Mockito.anyString()))).thenReturn(findObservableUser);
        when(findObservableUser.toObservable())
                .thenReturn(Observable.just(new Document(
                        Map.of("id", "1",
                                "name", "tt",
                                "login", "rr",
                                "currency", "RUB"))));
        when(findObservableProduct.toObservable())
                .thenReturn(Observable.just(new Document(
                        Map.of("id", "1",
                                "title", "tit",
                                "price", "800",
                                "currency", "RUB"))));
        when(request.getDecodedPath()).thenReturn("/product/get");
        when(request.getQueryParameters()).thenReturn(Map.of("id", List.of("1"), "user-id", List.of("1")));

        handler.makeRequest(request).toBlocking().getIterator().forEachRemaining(x -> {
                assertEquals(x, "Product{id=1, title='tit', price='800', currency='RUB'}");
        });
    }

    @Test
    public void testProductAdd() {
        MongoCollection productCollection = PowerMockito.mock(MongoCollection.class);
        HttpServerRequest request = PowerMockito.mock(HttpServerRequest.class);

        when(db.getCollection("product")).thenReturn(productCollection);
        when(productCollection.insertOne(new Document(
                Map.of("id", "1",
                        "title", "tit",
                        "price", "900",
                        "currency", "RUB")))).thenReturn(Observable.just(Success.SUCCESS));
        when(request.getDecodedPath()).thenReturn("/product/add");
        when(request.getQueryParameters()).thenReturn(Map.of(
                "id", List.of("1"),
                "title", List.of("tit"),
                "price", List.of("900"),
                "currency", List.of("RUB")));

        handler.makeRequest(request).toBlocking().getIterator().forEachRemaining(x -> {
            assertEquals(x, "true");
        });
    }

    @Test
    public void testProductGet_CurrencyConverted() {
        MongoCollection productCollection = PowerMockito.mock(MongoCollection.class);
        MongoCollection userCollection = PowerMockito.mock(MongoCollection.class);
        FindObservable findObservableProduct = PowerMockito.mock(FindObservable.class);
        FindObservable findObservableUser = PowerMockito.mock(FindObservable.class);
        HttpServerRequest request = PowerMockito.mock(HttpServerRequest.class);

        when(db.getCollection("product")).thenReturn(productCollection);
        when(db.getCollection("user")).thenReturn(userCollection);
        when(productCollection.find(Filters.eq("id", Mockito.anyString()))).thenReturn(findObservableProduct);
        when(userCollection.find(Filters.eq("id", Mockito.anyString()))).thenReturn(findObservableUser);
        when(findObservableUser.toObservable())
                .thenReturn(Observable.just(new Document(
                        Map.of("id", "1",
                                "name", "tt",
                                "login", "rr",
                                "currency", "RUB"))));
        when(findObservableProduct.toObservable())
                .thenReturn(Observable.just(new Document(
                        Map.of("id", "1",
                                "title", "tit",
                                "price", "800",
                                "currency", "USD"))));
        when(request.getDecodedPath()).thenReturn("/product/get");
        when(request.getQueryParameters()).thenReturn(Map.of("id", List.of("1"), "user-id", List.of("1")));

        handler.makeRequest(request).toBlocking().getIterator().forEachRemaining(x -> {
            assertThat(x, startsWith("Product{id=1, title='tit', price="));
            assertThat(x, endsWith("currency='RUB'}"));
        });
    }
}
