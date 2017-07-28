package me.xhy.java.spring.cloud.biz.instance.controller;

import me.xhy.java.spring.cloud.biz.instance.properties.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xuhuaiyu on 2017/7/27.
 */
@RestController
@RequestMapping("/value")
public class BizInstanceController {

    @Autowired
    private Property property;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * 返回 properties 中的内容，也有可能是通过命令行参数传入的参数
     * @return
     */
    @RequestMapping("/properties")
    public String getValue() {
        return property.toString();
    }
}
