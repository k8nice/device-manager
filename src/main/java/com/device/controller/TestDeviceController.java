package com.device.controller;

import com.alibaba.fastjson.JSON;
import com.device.common.Const;
import com.device.config.EchoServerHandler;
import com.device.config.LogPrint;
import com.device.model.TestReturnResult;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 宁海博
 * @date: 2021/3/31 10:45
 * @description:
 */
@RestController
@Api(value = "测试设备controller层")
@RequestMapping("/")
public class TestDeviceController {


    @PostMapping(value = "/app/bind/result")
    @ApiOperation(value = "返回绑定结果接口", httpMethod = "POST")
    public String test(@RequestBody TestReturnResult deviceReturnResult) {
        if (ObjectUtils.isEmpty(deviceReturnResult)) {
            LogPrint.error("com.device.controller", "TestDeviceController", "testDeviceReturnResult", "传递的参数为null");
        }
        LogPrint.info("com.device.controller", "TestDeviceController", "testDeviceReturnResult", "设备测试接口" + ",传递进来的内容为:" + deviceReturnResult);
        ChannelHandlerContext ctx = EchoServerHandler.channelMap.get(deviceReturnResult.getDeviceId());
        try {
            LogPrint.print("发送的数据为" + Const.Pattern.End + JSON.toJSON(deviceReturnResult).toString() + Const.Pattern.End);
            if (ObjectUtils.isEmpty(ctx)) {
                LogPrint.print("通道不存在");
                return "ERROR";
            }
            ctx.writeAndFlush(Const.Pattern.Start + JSON.toJSON(deviceReturnResult).toString() + Const.Pattern.End);
            return "SUCCESS";
        } catch (Exception e) {
            LogPrint.error("com.device.controller", "TestDeviceController", "testDeviceReturnResult", e);
            return "ERROR";
        }
    }

}
