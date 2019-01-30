package com.yanglf.rabbit.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yanglf
 * @description
 * @since 2019/1/30
 **/
@Data
public class UserTicket implements Serializable {
    private String userId;
    private String ticketId;
    private Date createTime;
    private int status;
}
