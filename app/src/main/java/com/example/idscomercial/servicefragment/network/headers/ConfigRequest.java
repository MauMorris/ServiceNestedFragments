package com.example.idscomercial.servicefragment.network.headers;

import java.util.HashMap;

public class ConfigRequest {
    private String keyUrl;
    private String keyUrlValue;
    private HashMap<String, String> header;
    public ConfigRequest() {
    }

    public String getKeyUrl() {
        return keyUrl;
    }

    public String getKeyUrlValue() {
        return keyUrlValue;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    public void setUrl(String keyUrl, String keyUrlValue) {
        this.keyUrl = keyUrl;
        this.keyUrlValue = keyUrlValue;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }
}