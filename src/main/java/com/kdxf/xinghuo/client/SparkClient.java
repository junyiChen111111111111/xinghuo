package com.kdxf.xinghuo.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdxf.xinghuo.client.request.SparkRequestBody;
import com.kdxf.xinghuo.client.response.SparkResponse;
import com.kdxf.xinghuo.utils.HttpUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SparkClient {
    private static final Logger logger = Logger.getLogger(SparkClient.class.getName());
    private static final String URL_KEY = "apiUrl";
    private static final String API_PASSWORD_KEY = "APIPassword";
    private final String apiUrl;
    private final String apiPassword;
    private final ObjectMapper mapper = new ObjectMapper();

    public SparkClient() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("client.yml");
        Map<String, String> clientConfig = yaml.load(inputStream);
        this.apiUrl = clientConfig.get(URL_KEY);
        this.apiPassword = clientConfig.get(API_PASSWORD_KEY);
        if (this.apiPassword == null || this.apiPassword.isEmpty()) {
            throw new IllegalArgumentException("未检测到APIPassword，请检查client.yml");
        }
        if (this.apiUrl == null || this.apiUrl.isEmpty()) {
            throw new IllegalArgumentException("未检测到APIUrl，请检查client.yml");
        }
    }

    public String httpPost(String text) {
        try {
            Map<String, String> headers = createHeaders(apiPassword);
            SparkRequestBody sparkRequestBody = createRequestBody(text);
            String jsonString = mapper.writeValueAsString(sparkRequestBody);

            SparkResponse result = HttpUtils.postMap(apiUrl, headers, jsonString);
            if (result == null) {
                return "请求失败，请检查相关配置";
            }
            if (result.getCode() != 0) {
                return "请求失败，code=" + result.getCode() + " message=" + result.getMessage();
            }
            return result.getChoices().get(0).getMessage().getContent();
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "JSON处理异常", e);
            return "JSON处理异常: " + e.getMessage();
        }
    }

    private Map<String, String> createHeaders(String apiPassword) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiPassword);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private SparkRequestBody createRequestBody(String text) {
        SparkRequestBody sparkRequestBody = new SparkRequestBody();
        sparkRequestBody.setModel("lite");
        SparkRequestBody.Message message = new SparkRequestBody.Message();
        message.setRole("user");
        message.setContent(text);
        sparkRequestBody.setMessages(List.of(message));
        return sparkRequestBody;
    }
}
