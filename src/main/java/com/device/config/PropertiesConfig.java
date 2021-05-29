package com.device.config;

import com.device.common.PropertiesUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author: 宁海博
 * @date: 2021/5/11 11:51
 * @description:
 */
@Configuration
public class PropertiesConfig {
    @Resource
    private Environment env;

    @PostConstruct
    public void setProperties() {
        PropertiesUtil.setEnvironment(env);
    }

}
