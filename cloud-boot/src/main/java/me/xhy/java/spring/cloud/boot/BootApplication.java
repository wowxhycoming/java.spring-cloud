package me.xhy.java.spring.cloud.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xuhuaiyu on 2017/7/25.
 */
@SpringBootApplication
@RestController
@RequestMapping("/")
public class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    @RequestMapping("/greet")
    public String greet() {
        return "hi there";
    }

}
