package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * clazz_leader_info
 *
 * @author
 */
@Data
public class ClazzLeaderInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 组长的id
     */
    private Integer leaderId;
    /**
     * 组员id
     */
    private Integer memberId;
}