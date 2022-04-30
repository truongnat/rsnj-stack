package com.peanut21.rjrstack.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collection;
import java.util.Collections;

public class HttpUtils {

    private static String apiHost = "coinranking1.p.rapidapi.com";
    private static String apiKey = "e39b977690msha51f48322b35200p1b9e05jsn1f3b508bccff";

    public static HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-RapidAPI-Host",apiHost);
        headers.set("X-RapidAPI-Key",apiKey);
        return new HttpEntity<>(null,headers);
    }
}
