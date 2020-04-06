package com.erokhina.manager;

import com.erokhina.common.http.ServerRunner;
import com.erokhina.manager.http.HttpMappingHandler;
import com.github.vanbv.num.json.JsonParserDefault;

public class Main {
    private static final int PORT = 8090;

    public static void main(String[] args) throws Exception {
        ServerRunner.runServer(PORT, new HttpMappingHandler(new JsonParserDefault()));
    }
}
