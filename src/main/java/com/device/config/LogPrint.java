package com.device.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: 宁海博
 * @date: 2021/4/24 19:13
 * @description: 自定义日志规范,这里写的不是很好,大家可以进行修改
 */
public class LogPrint {

    public static void print(Object log) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info(log + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }

    public static void info(String packageName,String className,String methodName,String msg) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info("当前的包名为:" + packageName + ",当前的类名为:" + className + "当前的方法名为:" + methodName+",需要打印的信息为:"+msg + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }

    public static void info(String packageName,String className,String methodName,String paramName,String msg) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info("当前的包名为:" + packageName + ",当前的类名为:" + className + "当前的方法名为:" + methodName+",参数名为:"+paramName+",需要打印的信息为:"+msg + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }

    public static void error(String packageName,String className,String methodName,String exMsg) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info("当前的包名为:" + packageName + ",当前的类名为:" + className + "当前的方法名为:" + ",需要打印的错误信息为:"+exMsg + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }

    public static void error(String packageName,String className,String methodName,Throwable throwable) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info("当前的包名为:" + packageName + ",当前的类名为:" + className + "当前的方法名为:" + ",需要打印的堆栈信息为:"+throwable.getStackTrace() + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }

    public static void error(String packageName,String className,String methodName,Throwable throwable,String exMsg) {
        Logger logger = LoggerFactory.getLogger(LogPrint.class);
        logger.info("当前的包名为:" + packageName + ",当前的类名为:" + className + "当前的方法名为:" + "错误信息为:"+exMsg+",需要打印的堆栈信息为:"+throwable.getStackTrace() + ",当前时间为:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH时 mm分 ss秒")));
    }
}
