package com.erokhinav.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StubSearchClient implements SearchClient {
    String searchEngine;

    public StubSearchClient(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    @Override
    public String makeSearchRequest(String request) {
        String result = "";
        try {
            result = new String(Files.readAllBytes(
                    Paths.get(String.format("src/main/resources/search-result/%s.json", searchEngine))));
        } catch (IOException e) {
            System.err.println("Unable to fetch search result from server. Search engine: " + searchEngine);
        }

        return result;
    }
}
