package com.vtmer.microteachingquality.controller;

import com.hung.microoauth2commons.commonutils.api.Result;
import com.vtmer.microteachingquality.service.NotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : Gking
 * @date : 2022-04-24 15:15
 **/
@Api("获取消息通知相关接口")
@RestController
@RequestMapping("/notify")
@Slf4j
public class NotifyController {

    @Resource
    private NotifyService notifyService;



    @ApiOperation("专业评审专家获取通知")
    @GetMapping("/getNotificationByMajorExpert")
    public Result<String> getNotificationByMajorExpert(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return notifyService.queryNotificationByMajorExpert(max, offset);
    }

    @ApiOperation("专业负责人获取通知")
    @GetMapping("/getNotificationByPrincipal")
    public Result<String> getNotificationByMajorPrincipal(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return notifyService.queryNotificationByMajorLeader(max, offset);
    }

    @ApiOperation("课程评审专家获取通知")
    @GetMapping("/getNotificationByExpert")
    public Result<String> getNotificationByClazzExpert(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return notifyService.queryNotificationByClazzExpert(max, offset);
    }

    @ApiOperation("课程负责人获取通知")
    @GetMapping("/getNotificationByPrincipal")
    public Result<String> getNotificationByClazzPrincipal(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return notifyService.queryNotificationByClazzLeader(max, offset);
    }


}
