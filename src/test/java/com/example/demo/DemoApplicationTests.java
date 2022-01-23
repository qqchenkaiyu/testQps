package com.example.demo;

import com.example.demo.util.QPSContext;
import com.example.demo.util.QPSUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    QPSUtil qpsUtil;
    @Test
    void contextLoads() {
        QPSContext qpsContext = new QPSContext();
        qpsContext.setExpectQps(20000);
      //  qpsContext.setInfinitily(false);

        qpsContext.setUrl("http://localhost:8080/index");
        qpsUtil.QPSTest(qpsContext);
    }

}
