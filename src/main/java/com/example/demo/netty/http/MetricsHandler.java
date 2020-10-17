/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 */

package com.example.demo.netty.http;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 并发控制
 *
 * @author g30000502
 * @since 2020/4/26
 */
@ChannelHandler.Sharable
public class MetricsHandler extends ChannelDuplexHandler {
    private AtomicLong totalConnectionNumber = new AtomicLong();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (totalConnectionNumber.incrementAndGet() <= RestfulParam.MAX_CONNECTION) {
            super.channelActive(ctx);
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.decrementAndGet();
        super.channelInactive(ctx);
    }

    /**
     * 获取连接数
     *
     * @return 连接数
     */
    public long getConnectionNum() {
        return totalConnectionNumber.get();
    }
}
