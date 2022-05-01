package com.hung.microoauth2commons.commonutils.utils;

/**
 * @author : Gking
 * @date : 2022-04-22 16:30
 **/
public class RedisConstants {
    public static final String USER_CODE_KEY ="register:code";
    public static final Long USER_CODE_TTL = 2L;
    public static final String FORGET_CODE_KEY ="forget:code";
    public static final Long FORGET_CODE_TTL = 2L;
    public static final String FEED_CLAZZ_EXPERT_KEY = "feedClazzExpert:";
    public static final String FEED_CLAZZ_LEADER_KEY = "feedClazzLeader:";
    public static final String FEED_MAJOR_EXPERT_KEY = "feedMajorExpert:";
    public static final String FEED_MAJOR_LEADER_KEY = "feedMajorLeader:";
    public static final String LOCK_NODE_KEY = "lock:node:";
    public static final String CACHE_NODE_KEY = "cache:node:";
    public static final Long CACHE_NULL_TTL = 5L;
    public static final Long CACHE_NODE_TTL = 30L;

}
