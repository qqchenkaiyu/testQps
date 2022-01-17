package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        QPSContext qpsContext = new QPSContext();
        qpsContext.setExpectQps(3000);
        qpsContext.setUrl("https://localhost:8080/index");
        qpsUtil.QPSTest(qpsContext);
    }

}
