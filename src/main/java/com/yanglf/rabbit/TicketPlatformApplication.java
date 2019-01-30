package com.yanglf.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class TicketPlatformApplication implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TicketPlatformApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        setTicket();
    }

    @Async
    public void setTicket() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("ticketNum",5+"");
        log.info("init ticket.....");

    }
}

