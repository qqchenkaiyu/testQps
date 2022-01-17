package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2019/3/20.
 */
@RestController
public class IndexController {
    @RequestMapping("/index")
    public ResponseEntity helloWord() {
        return ResponseEntity.ok().build();
    }
}
