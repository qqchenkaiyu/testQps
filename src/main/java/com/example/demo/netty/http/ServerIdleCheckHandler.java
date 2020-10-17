/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 */

package com.example.demo.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author g30000502
 * @since 2020/3/2
 */
public class ServerIdleCheckHandler extends IdleStateHandler {
    /**
     * 心跳保护构造方法
     */
    public ServerIdleCheckHandler() {
        super(5, 0, 0, TimeUnit.SECONDS);
    }
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
