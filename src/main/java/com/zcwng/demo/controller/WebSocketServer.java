package com.zcwng.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcwng.demo.util.RedisReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@EnableScheduling
@ServerEndpoint(value = "/websocket/{userid}")
@Component
public class WebSocketServer { //每个人会分配一个独立的实例


    static RedisReceiver redisReceiver;
    @Autowired void setRedisReceiver(RedisReceiver redisReceiver) {
        WebSocketServer.redisReceiver=redisReceiver;
    }


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(@PathParam("userid") Long userid, Session session) {
        redisReceiver.onOpen(session,userid);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        redisReceiver.onClose(session);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        redisReceiver.onMessage(message,session);
    }




     @OnError
     public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
     }
}
