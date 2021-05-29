package com.device.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 手表通信服务实现类
 *
 * @author 宁海博
 */

public interface DeviceService {

    /**
     * 根据状态码类型分发
     * @param code
     * @param json
     * @param deviceId
     * @param channelId
     * @param ctx
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public Object checkCodeType(String code, JSONObject json, String deviceId, ChannelId channelId, ChannelHandlerContext ctx) throws IOException, URISyntaxException;
}
