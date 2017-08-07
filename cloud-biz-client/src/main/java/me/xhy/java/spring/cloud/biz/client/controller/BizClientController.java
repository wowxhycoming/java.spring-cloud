package me.xhy.java.spring.cloud.biz.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

/**
 * Created by xuhuaiyu on 2017/7/28.
 */
@RestController
@RequestMapping("/view")
public class BizClientController {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    String instanceName = "CLOUD-BIZ-INSTANCE";
    String resourceUri = "/value/properties/";
    String resourcePosition = "http://CLOUD-BIZ-INSTANCE/value/properties/";


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

        // 1. discovery client
        String discoveryClientResult = "";
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(instanceName);
        if (serviceInstances != null && serviceInstances.size() > 0 ) {
            URI uri = serviceInstances.get(0).getUri();
            if (uri !=null ) {
                discoveryClientResult = (new RestTemplate()).getForObject(uri+resourceUri,String.class);
            }
        }

        // 2. LoadBalanced
        String LoadBalancedResult = restTemplate.getForObject(resourcePosition,String.class);

        // 3. Ribbon
        ServiceInstance instance = loadBalancerClient.choose(instanceName);
        String ribbonResult = (new RestTemplate()).getForObject(instance.getUri()+resourceUri,String.class);

        return
                "discoveryClientResult:" + discoveryClientResult + "|" +
                "LoadBalancedResult:" + LoadBalancedResult + "|" +
                "ribbonResult:" + ribbonResult + "|";
    }
}
