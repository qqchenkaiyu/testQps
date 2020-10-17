package com.example.demo.netty.rpc.consumer;


import com.example.demo.netty.rpc.api.IRpcHelloService;
import com.example.demo.netty.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {
	
    public static void main(String [] args){  
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);
        
        System.out.println(rpcHello.hello("Tom老师"));

    }
    
}
