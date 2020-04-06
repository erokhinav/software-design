package com.erokhina.visit;

import com.erokhina.common.http.ServerRunner;
import com.erokhina.visit.http.HttpMappingHandler;
import com.github.vanbv.num.json.JsonParserDefault;

public class Main {
    private static final int PORT = 8091;

    public static void main(String[] args) throws Exception {
        ServerRunner.runServer(PORT, new HttpMappingHandler(new JsonParserDefault()));
    }
}
