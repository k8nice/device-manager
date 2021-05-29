package com.device.common;

/**
 * @Author: 宁海博
 * @Date: 2021/5/8
 * @Description:    标识符
 */
public class Const {
    /**
     * 格式模板
     */
    public interface Pattern {
        // 请求头
        String Start = "####";
        // 请求尾
        String End = "$$$$";
    }

    /**
     * 设备请求码
     */
    public interface DeviceCode {
        // 状态码1
        String code10001 = "10001";
        // 时间校准
        String code10002 = "10002";
    }

    /**
     * 错误代码
     */
    public interface ErrorCode {
        // 未读取到deviceId
        String DEVICE_ID_NOT_FOUND = "500";
    }

}
