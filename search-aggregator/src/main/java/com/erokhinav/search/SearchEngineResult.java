package com.erokhinav.search;

import java.util.List;

public class SearchEngineResult {
    String engineName;
    List<Item> urls;

    SearchEngineResult(String engineName, List<Item> urls) {
        this.engineName = engineName;
        this.urls = urls;
    }

    public String getEngineName() {
        return engineName;
    }

    public List<Item> getUrls() {
        return urls;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SearchEngineResult result = (SearchEngineResult) obj;
        if (urls.size() != result.urls.size()) return false;

        for (int i = 0; i < urls.size(); i++) {
            if (!urls.get(i).equals(result.urls.get(i))) return false;
        }
        return engineName.equals(result.engineName);
    }

    public static class Item {
        String title;
        String url;

        Item(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            return title.equals(item.title) && url.equals(item.url);
        }
    }
}
