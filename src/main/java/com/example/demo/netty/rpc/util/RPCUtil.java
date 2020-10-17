package com.example.demo.netty.rpc.util;

import com.example.demo.netty.rpc.consumer.proxy.RpcProxyHandler;
import com.example.demo.netty.rpc.protocol.InvokerProtocol;
import com.example.demo.netty.rpc.registry.RegistryHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Method;

/**
 * @author: chenkaiyu
 * @create: 2019-07-02 10:47
 */
public class RPCUtil {
	/**
	 * 实现接口的核心方法

	 * @return
	 */
	public static Object rpcInvoke(InvokerProtocol invokerProtocol){
		final RpcProxyHandler consumerHandler = new RpcProxyHandler();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			initClientBootstrap(consumerHandler, group, b);
			ChannelFuture future = b.connect("localhost", 8080).sync();
			future.channel().writeAndFlush(invokerProtocol).sync();
			future.channel().closeFuture().sync();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			group.shutdownGracefully();
		}
		return consumerHandler.getResponse();
	}

	private static void initClientBootstrap(final RpcProxyHandler consumerHandler, EventLoopGroup group, Bootstrap b) {
		b.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						//自定义协议解码器
						/** 入参有5个，分别解释如下
						 maxFrameLength：框架的最大长度。如果帧的长度大于此值，则将抛出TooLongFrameException。
						 lengthFieldOffset：长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
						 lengthFieldLength：长度字段的长度：如：长度字段是int型表示，那么这个值就是4（long型就是8）
						 lengthAdjustment：要添加到长度字段值的补偿值
						 initialBytesToStrip：从解码帧中去除的第一个字节数
						 */
						pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
						//自定义协议编码器
						pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
						//对象参数类型编码器
						pipeline.addLast("encoder", new ObjectEncoder());
						//对象参数类型解码器
						pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
						pipeline.addLast(consumerHandler);
					}
				});
	}
	public static ServerBootstrap initServerBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup, ServerBootstrap b) {
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						//自定义协议解码器
						/** 入参有5个，分别解释如下
						 maxFrameLength：框架的最大长度。如果帧的长度大于此值，则将抛出TooLongFrameException。
						 lengthFieldOffset：长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
						 lengthFieldLength：长度字段的长度。如：长度字段是int型表示，那么这个值就是4（long型就是8）
						 lengthAdjustment：要添加到长度字段值的补偿值
						 initialBytesToStrip：从解码帧中去除的第一个字节数
						 */
						pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
						//自定义协议编码器
						pipeline.addLast(new LengthFieldPrepender(4));
						//对象参数类型编码器
						pipeline.addLast("encoder",new ObjectEncoder());
						//对象参数类型解码器
						pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
						pipeline.addLast(new RegistryHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		return b;
	}
	public static InvokerProtocol getProtocol(String className, Method method, Object[] args) {
		//传输协议封装
		InvokerProtocol msg = new InvokerProtocol();
		msg.setClassName(className);
		msg.setMethodName(method.getName());
		msg.setValues(args);
		msg.setParames(method.getParameterTypes());
		return msg ;
	}
}
