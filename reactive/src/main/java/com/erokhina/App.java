package com.erokhina;

import com.erokhina.dao.CatalogDao;
import com.erokhina.handler.HttpHandler;
import com.erokhina.handler.HttpHandlerImpl;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import io.reactivex.netty.protocol.http.server.HttpServer;

public class App {
    private static final String DB = "catalog";
    private static MongoClient client = createMongoClient();
    private static HttpHandler handler = new HttpHandlerImpl(new CatalogDao(client.getDatabase(DB)));

    public static void main(String[] args) throws InterruptedException {
        HttpServer
            .newServer(8080)
            .start((req, resp) -> {
                System.out.println(req.getDecodedPath());
                return resp.writeString(handler.makeRequest(req));
            })
            .awaitShutdown();
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}
