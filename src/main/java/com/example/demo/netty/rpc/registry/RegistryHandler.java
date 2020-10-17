package com.example.demo.netty.rpc.registry;


import cn.hutool.core.util.ClassUtil;
import com.example.demo.netty.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class RegistryHandler  extends ChannelInboundHandlerAdapter {

	//用保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String,Object>();

    public RegistryHandler(){
    	//完成递归扫描
		Set<Class<?>> classes = ClassUtil.scanPackage("com.example.demo");
		classes.stream().forEach(aClass -> {
			try {
				registryMap.put(aClass.getInterfaces()[0].getName(), aClass.newInstance());
			} catch (Exception e) {
				log.error("",e);
			}
		});
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	Object result = new Object();
        InvokerProtocol request = (InvokerProtocol)msg;
        //当客户端建立连接时，需要从自定义协议中获取信息，拿到具体的服务和实参
		//使用反射调用
        if(registryMap.containsKey(request.getClassName())){
        	Object clazz = registryMap.get(request.getClassName());
        	Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
        	result = method.invoke(clazz, request.getValues());
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         cause.printStackTrace();
         ctx.close();
    }
}
