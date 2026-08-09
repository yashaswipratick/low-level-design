package com.lld.phase6.patterns.creational.builder.problem2;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;
    private final int timeoutSeconds;
    private final boolean followRedirects;

    private HttpRequest(HttpRequestBuilder b) {
        this.method = b.method;
        this.url = b.url;
        this.headers = Map.copyOf(b.headers);
        this.body = b.body;
        this.timeoutSeconds = b.timeoutSeconds;
        this.followRedirects = b.followRedirects;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", timeoutSeconds=" + timeoutSeconds +
                ", followRedirects=" + followRedirects +
                '}';
    }

    public static class HttpRequestBuilder {
        private String method;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private String body = null;
        private int timeoutSeconds = 30;
        private boolean followRedirects = true;

        public HttpRequestBuilder(String method, String url) {
            this.method = method;
            this.url = url;
        }

        public HttpRequestBuilder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public HttpRequestBuilder followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpRequest build() {
            if (!url.startsWith("http")) throw new IllegalArgumentException("URL must start with http");
            if (timeoutSeconds <= 0) throw new IllegalArgumentException("Timeout must be > 0");
            return new HttpRequest(this);
        }
    }
}
