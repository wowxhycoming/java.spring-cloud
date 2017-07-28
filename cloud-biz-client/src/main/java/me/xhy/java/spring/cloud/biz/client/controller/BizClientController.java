package me.xhy.java.spring.cloud.biz.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by xuhuaiyu on 2017/7/28.
 */
@RestController
@RequestMapping("/view")
public class BizClientController {

    @Autowired
    RestTemplate client;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return template;
    }

    @RequestMapping("/properties")
    public String getValue() {
        String result = client.getForObject("http://CLOUD-BIZ-INSTANCE/value/properties/",String.class);
        System.out.println("return from instance : " + result);

        return client.getForObject("http://CLOUD-BIZ-INSTANCE/value/properties/",String.class);
    }
}
