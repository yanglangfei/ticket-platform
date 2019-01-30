package com.yanglf.rabbit.listener;
import com.alibaba.fastjson.JSONObject;
import com.yanglf.rabbit.model.UserTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author yanglf
 * @description
 * @since 2019/1/30
 **/
@Component
@RabbitListener(queues = "ticket")
@Slf4j
public class UserTicketListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @RabbitHandler
    public void process(UserTicket userTicket) {
        System.out.println("Receiver  : " + userTicket.toString());
        // 处理抢票逻辑
        try {
            Thread.sleep(10000);
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            String ticketStr = ops.get(userTicket.getUserId() + ":" + userTicket.getTicketId());
            if(ticketStr==null){
                ops.set(userTicket.getUserId()+":"+userTicket.getTicketId(),JSONObject.toJSONString(userTicket));
            }
            Boolean hasTicket = stringRedisTemplate.hasKey("ticketNum");
            if(hasTicket==null || !hasTicket){
                noTicketHandle(userTicket, ops, 2);
                return;
            }

            String ticketNumStr = ops.get("ticketNum");
            if(ticketNumStr==null){
                noTicketHandle(userTicket, ops, 2);
                return;
            }
            int ticketNum = Integer.parseInt(ticketNumStr);
            if(ticketNum<=0){
                noTicketHandle(userTicket, ops, 2);
                return;
            }
            ops.set("ticketNum",(ticketNum-1)+"");
            userTicket.setCreateTime(new Date());
            noTicketHandle(userTicket, ops, 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info(userTicket.getUserId()+"--------------------------抢票失败----------------");
        }
    }

    private void noTicketHandle(UserTicket userTicket, ValueOperations<String, String> ops, int status) {
        userTicket.setStatus(status);
        ops.set(userTicket.getUserId() + ":" + userTicket.getTicketId(), JSONObject.toJSONString(userTicket));
        if(status==2){
            log.info(userTicket.getUserId() + "--------------------------票已被抢完----------------");
        }else if(status==1){
            log.info(userTicket.getUserId() + "--------------------------抢票成功----------------");
        }else{
            log.info(userTicket.getUserId()+"--------------------------票已被抢完----------------");
        }
    }

}
