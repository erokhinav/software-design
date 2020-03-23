package com.erokhina.handler;

import java.util.List;
import java.util.Map;

import com.erokhina.dao.CatalogDao;
import com.erokhina.model.Product;
import com.erokhina.model.User;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

public class HttpHandlerImpl implements HttpHandler {
    private CatalogDao dao;

    public HttpHandlerImpl(CatalogDao dao) {
        this.dao = dao;
    }

    @Override
    public Observable<String> makeRequest(HttpServerRequest<?> req) {
        Object res;
        var params = req.getQueryParameters();
        switch (req.getDecodedPath()) {
            case "/user/add":
                res = dao.userAdd(new User(
                        getParam(params, "id"),
                        getParam(params, "name"),
                        getParam(params, "login"),
                        getParam(params, "currency")));
                break;
            case "/user/get":
                res = dao.userById(getParam(params, "id"));
                break;
            case "/product/add":
                res = dao.productAdd(new Product(
                        getParam(params, "id"),
                        getParam(params, "title"),
                        getParam(params, "price"),
                        getParam(params, "currency")
                ));
                break;
            case "/product/get":
                res = dao.productById(
                        getParam(params, "user-id"),
                        getParam(params, "id")
                );
                break;
            case "/products":
                res = dao.productsGet(getParam(params, "user-id"),
                        Integer.parseInt(getParam(params, "limit")),
                        Integer.parseInt(getParam(params, "offset")));
                break;
            default:
                res = Observable.just("Wrong query: " + req.getDecodedPath());
        }
        return ((Observable<?>)res).map(Object::toString);
    }

    String getParam(Map<String, List<String>> params, String param) {
        return params.get(param).get(0);
    }
}
