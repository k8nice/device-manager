package com.device.common;

import com.device.config.LogPrint;
import org.springframework.core.env.Environment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 解决静态全局变量无法获取springboot配置文件属性的工具类
 * @author: 宁海博
 * @date: 2021/5/11 11:54
 * @description:
 */
public class PropertiesUtil {
    private static Environment env = null;

    public static void setEnvironment(Environment env) {
        PropertiesUtil.env = env;
    }

    public static String getProperty(String key) {
        String result="";
        try {
            result= URLDecoder.decode(PropertiesUtil.env.getProperty(key), "UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            LogPrint.print(e);
        }
        return result;
    }

}
