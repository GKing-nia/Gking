package com.vtmer.microteachingquality.model.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : Gking
 * @date : 2022-04-24 15:25
 **/
@Data
@TableName("notify")
public class Notify {
    private Integer id;

    private Integer senderId;

    private String senderName;

    private String targetName;

    private String targetBelong;

    private Integer recipientId;

    private String message;

    private DateTime createdAt;

    private Boolean status;

    private DateTime readAt;
}
