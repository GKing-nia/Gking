package com.vtmer.microteachingquality.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.hung.microoauth2commons.commonutils.api.Result;
import com.hung.microoauth2commons.commonutils.utils.RegexUtils;
import com.vtmer.microteachingquality.common.PageResponseMessage;
import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.model.dto.UserPrincipalDTO;
import com.vtmer.microteachingquality.model.dto.insert.InsertUserDTO;
import com.vtmer.microteachingquality.model.dto.insert.UserDTO;
import com.vtmer.microteachingquality.model.dto.insert.UserInsertDTO;

import com.vtmer.microteachingquality.model.dto.insert.ForgetPwdDTO;
import com.vtmer.microteachingquality.model.dto.update.UpdatePwdDTO;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Objects;

import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.FORGET_CODE_KEY;
import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.USER_CODE_KEY;
import static com.vtmer.microteachingquality.common.ResponseMessage.newSuccessInstance;

/**
 * @author eeatem
 */
@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @ApiOperation("获取当前登录账号的信息")
    @GetMapping("/getCurrentUserInfo")
    public ResponseMessage<UserPrincipalDTO> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String jsonStr = JSONUtil.toJsonStr(principal);
        UserPrincipalDTO userPrincipalDTO = JSON.parseObject(jsonStr, UserPrincipalDTO.class);
        return ResponseMessage.newSuccessInstance(userPrincipalDTO);
    }

    /*@ApiOperation(value = "创建账号")
    @PostMapping("/createAccount")
    public ResponseMessage createAccount(@RequestBody @Validated InsertUserDTO insertUserDTO) {
        User user = new User();
        BeanUtils.copyProperties(insertUserDTO, user);
        user.setIsClazz(insertUserDTO.getIsClazz());
        // 原始密码默认与账号名称相同
        user.setUserPwd(bCryptPasswordEncoder.encode(insertUserDTO.getUserName()));
        if (userService.saveUser(user) > 0) {
            return newSuccessInstance("注册成功");
        } else {
            return ResponseMessage.newErrorInstance("注册失败");
        }
    }*/

    @ApiOperation(value = "修改密码")
    @PutMapping("/password")
    public ResponseMessage updatePwd(@RequestBody @Validated UpdatePwdDTO updatePwdDTO) {
        // 获取当前登陆用户对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);

        if (!updatePwdDTO.getNewPwd().equals(updatePwdDTO.getNewPwdConfirm())) {
            return ResponseMessage.newErrorInstance("两次新密码输入不一致，修改密码失败");
        }
        if (bCryptPasswordEncoder.matches(updatePwdDTO.getNewPwdConfirm(), loginUser.getUserPwd())) {
            if (userService.updatePwd(loginUser, updatePwdDTO.getNewPwdConfirm()) > 0) {
                return newSuccessInstance("修改密码成功");
            }
        } else {
            return ResponseMessage.newErrorInstance("原始密码输入错误，修改密码失败");
        }
        return ResponseMessage.newErrorInstance("修改密码失败");

    }

    @ApiOperation(value = "分页获取全部账号信息")
    @GetMapping("/userInfo")
    public ResponseMessage<?> listUserInfo(@ApiParam("查询页数(第几页)") @Param(value = "pageNum") Integer pageNum,
                                           @ApiParam("单页查询数量") @Param(value = "pageSize") Integer pageSize) {
        if (pageNum != null && pageNum != null && pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
            return newSuccessInstance(PageResponseMessage.restPage(userService.listUserInfo()));
        } else {
            return newSuccessInstance(userService.listUserInfo());
        }
    }

    @ApiOperation(value = "(学校账号)重置指定用户密码", notes = "默认与用户名相同")
    @PutMapping("/password/{userId}")
    public ResponseMessage updatePwdByUserId(@ApiParam("用户账号id") @PathVariable("userId") Integer userId) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.SCHOOL.getType())) {
            return ResponseMessage.newErrorInstance("非学校账号，无法重置用户密码");
        }
        if (userService.updatePwdByUserId(userId) > 0) {
            return newSuccessInstance("账号密码重置成功");
        } else {
            return ResponseMessage.newErrorInstance("账号密码重置失败");
        }
    }


    @ApiOperation(value = "分页根据所属部门或指定专业获取账号信息")
    @GetMapping("/userInfoByUserBelong")
    public ResponseMessage<?> listUserInfoByUserBelong(@ApiParam(value = "所属部门或指定专业", required = true)
                                                       @NotBlank(message = "所属部门或指定专业不能为空")
                                                       @RequestParam(value = "userBelong") String userBelong,
                                                       @ApiParam("查询页数(第几页)") @Param(value = "pageNum") Integer pageNum,
                                                       @ApiParam("单页查询数量") @Param(value = "pageSize") Integer pageSize) {
        if (pageNum != null && pageNum != null && pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
            return newSuccessInstance(PageResponseMessage.restPage(userService.listUserInfoByUserBelong(userBelong)));
        } else {
            return newSuccessInstance(userService.listUserInfoByUserBelong(userBelong));
        }
    }

    @ApiOperation(value = "注销(删除)账号")
    @DeleteMapping("/{id}")
    public ResponseMessage cancelAccount(@ApiParam("账号id")
                                         @NotNull(message = "账号id不能为空")
                                         @PathVariable("id") Integer id) {
        if (userService.cancelAccount(id) > 0) {
            return newSuccessInstance("注销账号成功");
        } else {
            return ResponseMessage.newErrorInstance("注销账号失败");
        }
    }

//    @ApiOperation(value = "退出登陆")
//    @PostMapping("/logout")
//    public ResponseMessage logout() {
//        // 获取当前登陆主体
//        Subject subject = SecurityUtils.getSubject();
//        if (subject.isAuthenticated()) {
//            try {
//                subject.logout();
//                return newSuccessInstance("退出登陆成功");
//            } catch (Exception e) {
//                return ResponseMessage.newErrorInstance("退出登陆失败");
//            }
//        } else {
//            return newSuccessInstance("退出登陆成功");
//        }
//    }

    /*@ApiOperation("忘记密码")
    @PostMapping("/forgetPassword")
    public ResponseMessage forgetPassword(ForgetPwdDTO forgetPwdDTO) {
//        Integer state =
//        if (ObjectUtil.isNull(state) || state <= 0) {
//            return newErrorInstance("修改密码失败");
//        }else{
//            return newSuccessInstance("修改密码成功");
//        }
        return newSuccessInstance(userService.updatePwdByUserNameAndRealNameAndUserBelong(forgetPwdDTO.getUserId(), forgetPwdDTO.getName(), forgetPwdDTO.getUserBelong()));
    }*/

    @ApiOperation(value="发送验证码")
    @PostMapping("/send")
    public Result<String> sendCode(@RequestParam("email") String email){
        if (userService.sendCode(email)) {
            return Result.success("发送成功");
        }else {
            return Result.failed("发送失败，请稍后再试");
        }
    }

    @ApiOperation(value="忘记密码发送验证码验证")
    @PostMapping("/send")
    public Result<String> sendCodeWithForgetting(@RequestParam("email") String email){
        if (userService.sendCodeWithForgetting(email)) {
            return Result.success("发送成功");
        }else {
            return Result.failed("发送失败，请稍后再试");
        }
    }

    @ApiOperation(value = "创建注册账号")
    @PostMapping("/createAccount")
    public Result<String> createAccount(@RequestBody @Validated UserInsertDTO insertUserDTO) {
        UserDTO userDTO = new UserDTO();
        //检验邮箱格式是否正确
        String email = insertUserDTO.getEmail();
        if (RegexUtils.isEmailInvalid(email)){
            return Result.failed("邮箱格式错误");
        }
        //检验邮箱是否存在，存在返回错误
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        QueryWrapper<User> email1 = queryWrapper.eq("email", email);
        if ((userService.getOne(email1))!=null){
            return Result.failed("该邮箱已经被绑定！！！");
        }
        //确认两次密码输入是否一致
        if (!Objects.equals(insertUserDTO.getUserPwd(), insertUserDTO.getConfirmPassword())){
            return Result.failed("两次密码输入不一致！！！请重新输入密码！");
        }
        //从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(USER_CODE_KEY + email);
        String code = insertUserDTO.getCode();
        if (cacheCode==null||!cacheCode.equals(code)){
            return Result.failed("验证码错误");
        }
        BeanUtils.copyProperties(insertUserDTO, userDTO);
        userDTO.setIsClazz(insertUserDTO.getIsClazz());
        // 原始密码默认与账号名称相同
        //user.setUserPwd(bCryptPasswordEncoder.encode(insertUserDTO.getUserName()));
        if (userService.register(userDTO) > 0) {
            userService.putRole(userDTO.getId(),userDTO.getUserType());
            return Result.success("注册成功");
        } else {
            return Result.failed("注册失败");
        }
    }



    @ApiOperation("忘记密码重置密码")
    @PostMapping("/forgetPassword")
    public Result<String> forgetPasswordAndReset(@RequestBody ForgetPwdDTO forgetPwdDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        QueryWrapper<User> userQueryWrapper = queryWrapper.eq("user_name", forgetPwdDTO.getUserAccount())
                .eq("real_name", forgetPwdDTO.getName())
                .eq("email", forgetPwdDTO.getEmail());
        if (userService.getOne(userQueryWrapper)==null){
            return Result.failed("该用户不存在！");
        }
        //从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(FORGET_CODE_KEY + forgetPwdDTO.getEmail());
        String code = forgetPwdDTO.getCode();
        if (cacheCode==null||!cacheCode.equals(code)){
            return Result.failed("验证码错误");
        }
        //密码两次输入一致
        if (!Objects.equals(forgetPwdDTO.getConfirmPassword(), forgetPwdDTO.getPassword())){
            return Result.failed("两次密码输入不一致，请重新输入！！！");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper<User> set = userUpdateWrapper.eq("user_name", forgetPwdDTO.getUserAccount())
                .eq("real_name", forgetPwdDTO.getName())
                .eq("email", forgetPwdDTO.getEmail())
                .set("user_pwd", bCryptPasswordEncoder.encode(forgetPwdDTO.getPassword()));

        boolean update = userService.update(set);
        if (update){
            return Result.success("重置密码成功");
        }else {
            return Result.failed("重置密码失败！");
        }
    }



}
