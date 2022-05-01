package com.vtmer.microteachingquality.personneltraining.simpletest;

import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.insert.InsertUserDTO;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.ClazzService;
import com.vtmer.microteachingquality.service.ReportService;
import com.vtmer.microteachingquality.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 墨小小
 * @date 21-10-05 20:57
 */
@SpringBootTest
@Slf4j
public class PoiInsertData {

    @Resource
    private MajorMapper majorMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ClazzService clazzService;

    @Resource
    private ClazzOpinionRecordMapper clazzOpinionRecordMapper;

    @Resource
    private LeaderEvaluationMapper leaderEvaluationMapper;

    @Resource
    private MasterEvaluationMapper masterEvaluationMapper;

    @Resource
    private ReportService reportService;

    @Resource
    private ClazzMapper clazzMapper;

    @Resource
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //读取文件并进行批量注册
    @org.junit.jupiter.api.Test
    public void insertMajorArchiveUserData() throws Exception {
//        //登录
//        UserLoginDTO userLoginDTO = new UserLoginDTO();
//        userLoginDTO.setUserName("268494");
//        userLoginDTO.setUserPwd("123456");
//        userController.login(userLoginDTO);
        //读取
        String path = "G:\\1.xlsx";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rowSizes = sheet.getPhysicalNumberOfRows();
        for (int rowCounts = 1; rowCounts < rowSizes; rowCounts++) {
            Row row = sheet.getRow(rowCounts);
            if (row != null) {
                Cell majorNameCell = row.getCell(0);
                Cell majorCollegeCell = row.getCell(1);
                Cell accountCell = row.getCell(2);
                String majorName = majorNameCell.getStringCellValue();
                String majorCollege = majorCollegeCell.getStringCellValue();
                String account = accountCell.getStringCellValue();

                //查询专业是否存在
                Major major = majorMapper.selectByMajorName(majorName);
                //专业不存在
                if (major == null) {
                    major = new Major();
                    major.setCollege(majorCollege);
                    major.setName(majorName);
                    majorMapper.insert(major);
                    log.info("插入专业：{}", majorName);
                }
                InsertUserDTO insertUserDTO = new InsertUserDTO();
                insertUserDTO.setUserType("专业归档");
                insertUserDTO.setUserName(account);
                insertUserDTO.setUserBelong(majorName);
                insertUserDTO.setRealName(majorName);
                insertUserDTO.setIsClazz(0);
                User user = new User();
                BeanUtils.copyProperties(insertUserDTO, user);
                user.setIsClazz(insertUserDTO.getIsClazz());
                // 原始密码默认与账号名称相同
                user.setUserPwd(passwordEncoder.encode(user.getUserName()));
                if (userService.saveUser(user) > 0) {
                    log.info("注册成功:{}", majorName);
                } else {
                    log.info("注册失败:{}", majorName);
                }
            }
        }
        log.info("插入结束");
    }

    //插入课程负责人帐号信息
    @org.junit.jupiter.api.Test
    public void insertClazzArchiveData() throws Exception {
        String path = "课程评估第二批次(1).xlsx";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rowSizes = sheet.getPhysicalNumberOfRows();
        for (int rowCounts = 1; rowCounts < rowSizes; rowCounts++) {
            Row row = sheet.getRow(rowCounts);
            if (row != null) {
                String type = row.getCell(1).getStringCellValue();
                String clazzName = row.getCell(2).getStringCellValue();
                String college = row.getCell(3).getStringCellValue();
                String majorName = row.getCell(4).getStringCellValue();
                String account = row.getCell(5).getStringCellValue();
                //帐号密码相同
                String password = row.getCell(6).getStringCellValue();
                //查询课程是否存在
                Clazz clazz = clazzMapper.selectByName(clazzName);
                //课程不存在
                if (clazz == null) {
                    //创建课程对象并插入
                    clazz = new Clazz(college, majorName, clazzName, type);
                    if (clazzMapper.insert(clazz) > 0) {
                        log.info("插入课程：{}", clazzName);
                        //插入课程后，插入与评审专家的关系
                        //查询相应学科的课程评审
                        Integer clazzId = clazzMapper.selectClazzId(college, majorName, type, clazzName);
                        List<User> userList1 = userMapper.selectByUserTypeAndUserBelong("课程评审专家", clazz.getType() + "类课程评审专家");
                        List<User> userList2 = userMapper.selectByUserTypeAndUserBelong("课程评审专家组长", clazz.getType() + "类课程评审专家组长");
                        userList1.addAll(userList2);
                        userList1.forEach(user -> {
                            if (clazzExpertManageInfoMapper.insertManageInfo(user.getId(), clazzId) > 0) {
                                log.info("插入 expert_manage_info 关系成功 userId为:" + user.getId() + "  ClazzId为" + clazzId);
                            }
                        });
                    } else {
                        log.info("插入课程：{} 失败!!!", clazzName);
                    }
                }
                //
                InsertUserDTO insertUserDTO = new InsertUserDTO(account, clazzName, "课程负责人", clazzName, 1);
                User user = new User();
                BeanUtils.copyProperties(insertUserDTO, user);
                user.setIsClazz(insertUserDTO.getIsClazz());
                // 原始密码默认与账号名称相同
                user.setUserPwd(passwordEncoder.encode(user.getUserName()));
                if (userService.saveUser(user) > 0) {
                    log.info("注册成功:{}", clazzName);
                } else {
                    log.info("注册失败:{}", clazzName);
                }
            }
        }
        log.info("插入结束");
    }


    //课程评审导出报告
    @org.junit.jupiter.api.Test
    public void exportClazzData() throws Exception {
        List<ClazzOpinionRecord> clazzOpinionRecordList = clazzOpinionRecordMapper.selectAll();
        List<Integer> userIdList = new LinkedList<>();
        for (ClazzOpinionRecord clazzOpinionRecord : clazzOpinionRecordList) {
            User user = userMapper.selectByPrimaryKey(clazzOpinionRecord.getUserId());
            if (userIdList.contains(user.getId())) {
                continue;
            }
            userIdList.add(user.getId());
            XSSFWorkbook xssfWorkbook = clazzService.exportRecord(user.getId());
            FileOutputStream fileOutputStream = new FileOutputStream(user.getUserType() + "   " + user.getRealName() + "    " + new SimpleDateFormat("MM月dd日 HH时mm分ss秒").format(new Date()) + ".xlsx");
            xssfWorkbook.write(fileOutputStream);
        }
    }

    //专业导出报告
    @Test
    public void exportMajorData() throws Exception {
        List<LeaderEvaluation> leaderEvaluationList = leaderEvaluationMapper.selectAll();
        List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectAll();
        List<Integer> userIdList = new LinkedList<>();
        for (LeaderEvaluation leaderEvaluation : leaderEvaluationList) {
            Integer userId = leaderEvaluation.getUserId();
            if (userIdList.contains(userId)) {
                continue;
            }
            userIdList.add(userId);
            User user = userMapper.selectByPrimaryKey(userId);
            XSSFWorkbook workbook = reportService.exportRecord(userId);
            FileOutputStream fileOutputStream = new FileOutputStream(user.getUserType() + "   " + user.getRealName() + "    " + new SimpleDateFormat("MM月dd日 HH时mm分ss秒").format(new Date()) + ".xlsx");
            workbook.write(fileOutputStream);
        }
        userIdList.clear();
        for (MasterEvaluation masterEvaluation : masterEvaluationList) {
            Integer userId = masterEvaluation.getUserId();
            if (userIdList.contains(userId)) {
                continue;
            }
            userIdList.add(userId);
            User user = userMapper.selectByPrimaryKey(userId);
            XSSFWorkbook workbook = reportService.exportRecord(userId);
            FileOutputStream fileOutputStream = new FileOutputStream(user.getUserType() + "   " + user.getRealName() + "    " + new SimpleDateFormat("MM月dd日 HH时mm分ss秒").format(new Date()) + ".xlsx");
            workbook.write(fileOutputStream);
        }
    }
}
