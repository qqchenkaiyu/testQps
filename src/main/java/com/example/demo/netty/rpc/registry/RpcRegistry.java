package com.example.demo.netty.rpc.registry;

import com.example.demo.netty.rpc.util.RPCUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class RpcRegistry {  
    private int port;  
    public RpcRegistry(int port){  
        this.port = port;  
    }  
    public void start(){
        //React主从线程模型  bossGroup 接收请求  workerGroup 处理请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
          
        try {  
            ServerBootstrap b = new ServerBootstrap();
            RPCUtil.initServerBootstrap(bossGroup, workerGroup, b);
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
        new RpcRegistry(8080).start();    
    }    
}  
