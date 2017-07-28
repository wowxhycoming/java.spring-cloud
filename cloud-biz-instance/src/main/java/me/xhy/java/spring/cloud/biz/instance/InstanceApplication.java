package me.xhy.java.spring.cloud.biz.instance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by xuhuaiyu on 2017/7/27.
 */
@SpringBootApplication
@EnableEurekaClient
public class InstanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstanceApplication.class, args);
    }

}
