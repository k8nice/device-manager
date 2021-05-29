package com.device.service;

import com.alibaba.fastjson.JSONObject;
import com.device.common.Const;
import com.device.config.EchoServerHandler;
import com.device.config.LogPrint;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *设备服务 这里为了方便大家开发，没有加入任何业务代码
 *
 * @author 宁海博
 */
@Service
public class DeviceServiceImpl implements DeviceService {


    private final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();






    public DeviceServiceImpl() {
    }


    /**
     * 根据状态码类型分发
     * @param code 状态码
     * @param json JSONObject
     * @return JSONObject
     * @throws IOException
     * @throws URISyntaxException
     */
    public Object checkCodeType(String code, JSONObject json, String deviceId, ChannelId channelId, ChannelHandlerContext ctx) throws IOException, URISyntaxException {
        LogPrint.info("com.device.service", "DeviceService", "checkCodeType", "根据状态码来分发策略");
        if (StringUtils.isBlank(deviceId) || ObjectUtils.isEmpty(json) || StringUtils.isBlank(code) || ObjectUtils.isEmpty(ctx)) {
            LogPrint.print("参数不能为null");
            return null;
        }
        if (!EchoServerHandler.channelMap.containsKey(deviceId)) {
            EchoServerHandler.channelMap.put(deviceId, ctx);
        }
        switch (code) {
            case Const.DeviceCode.code10001:
                return test1();
            case Const.DeviceCode.code10002:
                return test2();
            default:
                throw new RuntimeException("发送失败");
        }
    }

    private Object test2() {
        return null;
    }

    private Object test1() {
        return null;
    }


}
