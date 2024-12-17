package com.kdxf.xinghuo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/getauth")
    public String getAuth(){

        return "{\"Authorization\":\"DfwAzbWXGkEFblhapNYD:WAqWhgQVvsUiOKDPzaTK\"}";
    }
}
