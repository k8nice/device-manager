package com.device;


import com.device.config.NettyServer;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * 启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableLogRecord(tenant = "com.device.service")
public class Application implements CommandLineRunner {

    @Value("${netty_port}")
    private  Integer netty_port;

    /**
     * 构造方法 解决netty无法注入的问题
     */
    public Application() {

    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private NettyServer nettyServer;


    @Override
    public void run(String... args) throws Exception {
        nettyServer.setPort(netty_port);
        nettyServer.start();
    }
}
