package com.kdxf.xinghuo.controller;

import com.kdxf.xinghuo.client.SparkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XingHuoController {

    @Autowired
    private SparkClient sparkClient;

    @PostMapping("/spark/lite")
    public String httpPost(@RequestBody String text) {
        if (text.isEmpty()){
            return "请输入内容";
        }
        return sparkClient.httpPost(text);
    }

}