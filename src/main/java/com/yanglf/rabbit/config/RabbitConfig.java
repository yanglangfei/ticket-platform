package com.yanglf.rabbit.config;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author yanglf
 * @description
 * @since 2019/1/30
 **/
@Component
public class RabbitConfig {

    @Bean
    public Queue queue() {
        return new Queue("ticket");
    }



}
