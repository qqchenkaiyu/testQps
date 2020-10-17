/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 */

package com.example.demo.netty.http;

/**
 * @author g30000502
 * @since 2020/3/18
 */
public class RestfulParam {
    /**
     * request最大初始化长度
     */
    public static final int MAX_INITIALLINE_LENGTH = 4096;

    /**
     * request 请求头最大长度
     */
    public static final int MAX_HEADER_SIZE = 8192;

    /**
     * MAX_CHUNK_SIZE
     */
    public static final int MAX_CHUNK_SIZE = 1024 * 1024;

    /**
     * MAX_CONTENT_LENGTH
     */
    public static final int MAX_CONTENT_LENGTH = 1024 * 1024;

    /**
     * 最大连接数
     */
    public static final long MAX_CONNECTION = 10000L;
}
