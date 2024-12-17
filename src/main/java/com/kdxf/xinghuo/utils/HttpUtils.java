package com.kdxf.xinghuo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdxf.xinghuo.client.response.SparkResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static SparkResponse postMap(String url, Map<String, String> headerMap, String body) {
        HttpPost post = new HttpPost(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                post.addHeader(entry.getKey(), entry.getValue());
            }
            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    return mapper.readValue(response.getEntity().getContent(), SparkResponse.class);
                } else {
                    String errorMessage = "Unexpected response status code: " + response.getStatusLine().getStatusCode();
                    logger.error(errorMessage);
                    throw new IOException(errorMessage);
                }
            }
        } catch (IOException e) {
            logger.error("Error in HTTP POST request", e);
            return null;
        }
    }

    public static Map<String, String> paramToMap(String paramStr) {
        String[] params = paramStr.split("&");
        Map<String, String> resultMap = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length >= 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    public static Map<String, String> splitHeaders(String headers) {
        String[] lines = headers.split("\n");
        Map<String, String> headerMap = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                headerMap.merge(key, value, (oldValue, newValue) -> oldValue + "," + newValue);
            }
        }
        return headerMap;
    }
}
