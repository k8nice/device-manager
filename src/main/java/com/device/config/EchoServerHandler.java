package com.device.config;

import com.alibaba.fastjson.JSONObject;
import com.device.common.Const;
import com.device.service.DeviceService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author 宁海博
 */
@Component
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    public static DeviceService deviceService;

    public static RedisTemplate redisTemplate;

    public final static ConcurrentMap<String, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    /**
     * 集合用来通过通道id和deviceId作为键值，作用是如果通道id或者deviceId存在集合，则说明是激活状态，第二个是通过通道id获取deviceId，可以防止线程不安全问题
     */
    public volatile static Map<ChannelId, String> map = new ConcurrentHashMap<>();


    /**
     * 解决netty无法注入的问题
     */
    @PostConstruct
    public void init1() {
        EchoServerHandler.map = new ConcurrentHashMap<>();
    }

    /**
     * 构造方法 解决netty无法注入的问题
     */
    public EchoServerHandler() {

    }

    /**
     * 初始化业务接口，避免springboot和netty整合无法注入bean的问题
     *
     * @param deviceService
     */
    @Autowired
    public void init(DeviceService deviceService, RedisTemplate redisTemplate) {
        EchoServerHandler.deviceService = deviceService;
        EchoServerHandler.redisTemplate = redisTemplate;
    }


    /*
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        LogPrint.print("当前线程数为:" + threadGroup.activeCount());
        LogPrint.print("传入连接的机器名和ip为" + ctx.channel().remoteAddress().toString() + " 通道已激活!");

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                LogPrint.print("心跳超时");
                ctx.close();
                LogPrint.print("断开长连接");
                if (map.containsKey(ctx.channel().id()) && !Objects.isNull(map.get(ctx.channel().id()))) {
                    channelMap.remove(map.get(ctx.channel().id()));
                    map.remove(ctx.channel().id());
                }
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /*
     *
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogPrint.print("传入连接的机器名和ip为" + ctx.channel().remoteAddress().toString() + " 通道不活跃!");
        //这里为了防止集合中没有移除通道，在方法中再次移除一次
        if (map.containsKey(ctx.channel().id()) && !Objects.isNull(map.get(ctx.channel().id()))) {
            channelMap.remove(map.get(ctx.channel().id()));
            map.remove(ctx.channel().id());
        }
    }

    /**
     * @param buf
     * @return 读取的信息
     * @author Taowd
     */
    private synchronized String getMessage(ByteBuf buf) {
        Integer byteLength = buf.readableBytes();
        byte[] con = new byte[byteLength];
        LogPrint.print("读取到的字节数为" + byteLength);
        buf.readBytes(con);
        try {
            return new String(con, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogPrint.print("ERROR 错误堆栈信息为" + e.getStackTrace());
            return null;
        }
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ObjectUtils.isEmpty(msg)) {
            LogPrint.print("报文为null");
        }
        LogPrint.print("msg=" + String.valueOf(msg));
        String str = new String((byte[]) msg, Charset.forName("UTF-8"));
        LogPrint.print(str);
        //去掉tcp报文的头，尾部用来处理粘包分包时已被截取
        str = str.replace(Const.Pattern.Start, "");
        String rev = str;
        String message = "";
        if (StringUtils.isNotBlank(rev)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(rev);
                //如果通道id不存在集合，并且传递过来的json中存在deviceId标识，则把deviceId添加到集合中,然后继续执行业务,否则直接执行业务
                if (!map.containsKey(ctx.channel().id()) && jsonObject.containsKey("deviceId")) {
                    channelMap.put(jsonObject.get("deviceId").toString(), ctx);
                    map.put(ctx.channel().id(), jsonObject.get("deviceId").toString());
                    Object jsonObject1 = null;
                    jsonObject1 = deviceService.checkCodeType(jsonObject.get("code").toString(), jsonObject, map.get(ctx.channel().id()), ctx.channel().id(), ctx);
                    if (!ObjectUtils.isEmpty(jsonObject1)) {
                        message = String.valueOf(jsonObject1);
                    }
                } else {
                    LogPrint.print("channelId存在,继续执行业务");
                    Object jsonObject1 = deviceService.checkCodeType(jsonObject.get("code").toString(), jsonObject, map.get(ctx.channel().id()), ctx.channel().id(), ctx);
                    if (!ObjectUtils.isEmpty(jsonObject1)) {
                        LogPrint.print("发送给手表的tcp报文为:" + message);
                        message = String.valueOf(jsonObject1);
                    }
                }
                LogPrint.print("receiveData 服务器收到客户端数据:" + rev + "发送过来的ip为:" + ctx.channel().remoteAddress());
                if (!StringUtils.isBlank(message)) {
                    //把业务执行后返回的结果发送回客户端
                    ChannelFuture channelFuture = ctx.writeAndFlush(message);
                    if (channelFuture.isSuccess()) {
                        LogPrint.print("发送内容到手表成功");
                    } else {
                        LogPrint.print("发送失败");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogPrint.print("读通道异常:" + e.getStackTrace());
            }
        } else {
            LogPrint.print("rev为null");
            ctx.channel().read();
        }
    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LogPrint.print("服务端接收数据完毕.. ");
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        LogPrint.print(new StringBuilder("state:ERROR 异常信息：\r\n".length() + cause.getStackTrace().toString().length()).append("异常信息：\r\n").append(cause.getStackTrace()).toString() + "异常ip为:" + ctx.channel().remoteAddress());
    }
}