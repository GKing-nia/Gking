package com.vtmer.microteachingquality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hung.microoauth2commons.commonutils.api.Result;
import com.vtmer.microteachingquality.model.entity.Notify;

public interface NotifyService extends IService<Notify> {
    Boolean sendNotificationByClazzPrincipal(Integer userId);

    Result<String> queryNotificationByClazzExpert(Long max, Integer offset);

    Boolean sendNotificationByClazzExpertLeader(Integer userId, Integer leaderId);

    Result<String> queryNotificationByClazzLeader(Long max, Integer offset);

    Boolean sendNotificationByMajorLeader(Integer id);

    Result<String> queryNotificationByMajorExpert(Long max, Integer offset);

    Boolean sendNotificationByMajorExpert(Integer id);

    Result<String> queryNotificationByMajorLeader(Long max, Integer offset);

}


