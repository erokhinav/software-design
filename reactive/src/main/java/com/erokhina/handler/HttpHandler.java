package com.erokhina.handler;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

public interface HttpHandler {
    Observable<String> makeRequest(HttpServerRequest<?> req);
}
