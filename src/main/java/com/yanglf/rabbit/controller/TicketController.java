package com.yanglf.rabbit.controller;

import com.alibaba.fastjson.JSONObject;
import com.yanglf.rabbit.model.UserTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanglf
 * @description
 * @since 2019/1/30
 **/
@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketController {

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyTicket(String userId, String ticketId) {
        UserTicket userTicket = new UserTicket();
        userTicket.setTicketId(ticketId);
        userTicket.setUserId(userId);
        // TODO 参数校验
        amqpTemplate.convertAndSend("ticket", userTicket, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("userId", "0");
                log.info("消息发送成功");
                return message;
            }
        });
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        userTicket.setStatus(0);
        ops.set(userId+":"+ticketId, JSONObject.toJSONString(userTicket));
        return "ok";
    }


    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getStatus(String userId, String ticketId) {
        ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
        String ticketInfo = forValue.get(userId + ":" + ticketId);
        if(ticketInfo!=null){
            return ticketInfo;
        }
        return "fail";

    }


}
