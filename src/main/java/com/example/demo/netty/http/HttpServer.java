/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 */

package com.example.demo.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {
    private int port;
    public HttpServer(int port){
        this.port = port;
    }
    public void start(){
        //React主从线程模型  bossGroup 接收请求  workerGroup 处理请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1000);

        try {
            ServerBootstrap b = new ServerBootstrap();
            final MetricsHandler metricsHandler = new MetricsHandler();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("metricHandler", metricsHandler);
                        pipeline.addLast("idleHandler", new ServerIdleCheckHandler());
                        pipeline.addLast("decoder",
                            new HttpRequestDecoder(RestfulParam.MAX_INITIALLINE_LENGTH, RestfulParam.MAX_HEADER_SIZE,
                                RestfulParam.MAX_CHUNK_SIZE));
                        pipeline.addLast("encoder", new HttpResponseEncoder());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(RestfulParam.MAX_CONTENT_LENGTH));
                        pipeline.addLast(new HttpRequestHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(port).sync();
            System.out.println("GP RPC Registry start listen at " + port );
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
             bossGroup.shutdownGracefully();
             workerGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new HttpServer(8081).start();
    }
}
