package com.example.demo.bio;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class bioServer {


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(8080), 1000);

            System.out.println("启动监听 ---- http://localhost:8080 ");
            while (true) {
                Socket accept = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        InputStreamReader isr = new InputStreamReader(accept.getInputStream());
                        char[] charBuf = new char[1024];
                        int mark;
                        while ((mark = isr.read(charBuf)) != -1) {
                            // builder.append(charBuf, 0, mark);
                            if (mark < charBuf.length) {
                                break;
                            }
                        }
//                        System.out.println(builder);
                        OutputStream outputStream = accept.getOutputStream();
                        outputStream.write(("HTTP/1.1 200 OK\r\n" +  //响应头第一行
                                "Content-Type: text/html; charset=utf-8\r\n" +  //简单放一个头部信息
                                "\r\n" +  //这个空行是来分隔请求头与请求体的
                                "<h1>这是响应报文</h1>\r\n").getBytes());
                        outputStream.flush();
                        // isr.close();
                        outputStream.close();
                        accept.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }

        }


    }
}
