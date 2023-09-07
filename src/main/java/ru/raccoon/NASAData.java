package ru.raccoon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NASAData {
    private String url;

    public NASAData(
            @JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "url=" + url;
    }
}
