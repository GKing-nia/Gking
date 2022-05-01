package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vtmer.microteachingquality.common.constant.enums.UserTypeConstant;
import com.vtmer.microteachingquality.common.exception.clazz.ClazzFileNotExistException;
import com.vtmer.microteachingquality.common.exception.clazz.ExpertNotFoundException;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.ClazzService;
import com.vtmer.microteachingquality.util.UserUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: personneltraining2
 * @description:
 * @author: 周华娟
 * @create: 2021-07-26 00:19
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ClazzServiceImpl implements ClazzService, UserTypeConstant {

    @Autowired
    private ClazzFileTemplateMapper clazzFileTemplateMapper;
    @Autowired
    private ClazzAnnotationMapper clazzAnnotationMapper;
    @Autowired
    private ClazzFileMapper clazzFileMapper;
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Autowired
    private ClazzEvaluateOptionRecordMapper clazzEvaluateOptionRecordMapper;
    @Autowired
    private ClazzOpinionRecordMapper clazzOpinionRecordMapper;
    @Autowired
    private ClazzEvaluateOptionMapper clazzEvaluateOptionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EvaluationProcessMapper evaluationProcessMapper;

    @Override
    public List<ClazzAnnotation> getAnnotation() {
        return clazzAnnotationMapper.selectAll();
    }

    @Override
    public ClazzAnnotation getClazzAnnotationById(Integer id) {
        return clazzAnnotationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ClazzFileTemplate> getAllClazzTemplate() {
        return clazzFileTemplateMapper.selectAll();
    }

    @Override
    public List<ClazzFileTemplate> getClazzTemplate() {
        return clazzFileTemplateMapper.selectAll();
    }

    @Override
    public ClazzFile exitFile(Integer userId, Long evaluationId, String fileName) {
        QueryWrapper<ClazzFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("evaluation_id", evaluationId);
        queryWrapper.eq("file_name", fileName);
        return clazzFileMapper.selectOne(queryWrapper);
    }

    @Override
    public ClazzFile getClazzFile(Integer userId, String clazzName) {
        return clazzFileMapper.selectByClazzNameAndUserId(userId, clazzName);
    }

    @Override
    public int saveClazzFile(ClazzFile clazzFile) {
        return clazzFileMapper.insert(clazzFile);
    }

    @Override
    public ClazzFileTemplate getClazzFileTemplateByMajor(String major) {
        return clazzFileTemplateMapper.selectByMajor(major);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClazzFile(User loginUser, ClazzFile clazzFile) {
        ClazzFile clazzFile1 = clazzFileMapper.selectByClazzNameAndUserId(loginUser.getId(), clazzFile.getClazzName());
        if (ObjectUtil.isNull(clazzFile1)) {
            throw new ClazzFileNotExistException();
        }
        return clazzFileMapper.updateByPrimaryKey(clazzFile);
    }

    @Override
    public ClazzFileTemplate getClazzByMajor(String major) {
        return clazzFileTemplateMapper.selectByMajor(major);
    }

    @Override
    public Major getMajorByName(String name) {
        return majorMapper.selectByMajorName(name);
    }

    @Override
    public Clazz getClazzByName(String name) {
        return clazzMapper.selectByName(name);
    }


    /**
     * 通过课程评审专家id获取这个专家所有要评审的课程信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<GetEvaluationClazzByUserIdResult> getEvaluationClazzByUserId(Integer userId) {
        List<ClazzExpertManageInfo> clazzExpertManageInfoList = clazzExpertManageInfoMapper.selectByUserId(userId);
        if (ObjectUtil.isNull(clazzExpertManageInfoList) || clazzExpertManageInfoList.size() == 0) {
            throw new ExpertNotFoundException();
        }
        List<GetEvaluationClazzByUserIdResult> resultList = new ArrayList<>();
        for (ClazzExpertManageInfo clazzExpertManageInfoTemp : clazzExpertManageInfoList) {
            Integer clazzId = clazzExpertManageInfoTemp.getClazzId();
            Clazz clazz = clazzMapper.selectByPrimaryKey(clazzId);
            GetEvaluationClazzByUserIdResult resultTemp = new GetEvaluationClazzByUserIdResult();
            resultTemp.setClazzId(clazz.getId());
            resultTemp.setClazzMajor(clazz.getMajor());
            resultTemp.setClazzName(clazz.getName());
            resultTemp.setClazzType(clazz.getType());
            resultTemp.setClazzStatus(clazzExpertManageInfoTemp.getStatus());
            resultTemp.setCollege(clazz.getCollege());
            resultList.add(resultTemp);
        }
        return resultList;
    }

    @Override
    public List<GetAllClazzInfoResult> getAllClazzInformation() {
        List<Clazz> clazzList = clazzMapper.selectAll();
        List<GetAllClazzInfoResult> resultList = new ArrayList<>();
        clazzList.forEach(clazzTemp -> {
            List<ClazzFile> clazzFileList = clazzFileMapper.selectByClazzName(clazzTemp.getName());

            List<FileInfoResult> fileInfoResultList = clazzFileList.stream()
                    .map(clazzFile -> new FileInfoResult(clazzFile.getId(), clazzFile.getFileName(), clazzFile.getPath(), clazzFile.getUpdateTime()))
                    .collect(Collectors.toList());

            GetAllClazzInfoResult result = new GetAllClazzInfoResult(clazzTemp.getName(), clazzTemp.getId(), fileInfoResultList);
            resultList.add(result);
        });
        return resultList;
    }



    @Override
    public Integer deleteUploadedFile(String path) {
        return clazzFileMapper.deleteByPath(path);
    }

    @Override
    public XSSFWorkbook exportRecord(Integer userId) {
        XSSFWorkbook result = new XSSFWorkbook();

        QueryWrapper<ClazzEvaluateOptionRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.eq("user_id", userId);
        recordQueryWrapper.select("evaluation_id");
        //查询该用户参与了多少评审 EvaluationProcess
        List<ClazzEvaluateOptionRecord> clazzEvaluateOptionRecords = clazzEvaluateOptionMapper.selectList(recordQueryWrapper);
        clazzEvaluateOptionRecords.stream().distinct().forEach(clazzEvaluateOptionRecord -> {
            //查询出这个评审流程和课程 的详细信息
            EvaluationProcess evaluationProcess = evaluationProcessMapper.selectById(clazzEvaluateOptionRecord.getEvaluationId());
            Clazz clazz = clazzMapper.selectById(evaluationProcess.getClazzId());


            //获取这个专家 关于这个评审的文字意见
            QueryWrapper<ClazzOpinionRecord> opinionRecordQueryWrapper = new QueryWrapper<>();
            opinionRecordQueryWrapper.eq("evaluation_id", clazzEvaluateOptionRecord.getEvaluationId());
            opinionRecordQueryWrapper.eq("user_id", userId);
            ClazzOpinionRecord clazzOpinionRecord = clazzOpinionRecordMapper.selectOne(opinionRecordQueryWrapper);

            //每个批次都是一张Sheet
            Sheet sheet = result.createSheet(clazz.getName());
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            //设置基本信息
            //在第一行写入一些基本数据，例如更新时间，课程名字，
            titleCell.setCellValue("课程名：" + clazz.getName());
            titleCell = titleRow.createCell(1);
            titleCell.setCellValue("更新时间：" + clazzOpinionRecord.getUpdateTime());

            titleRow = sheet.createRow(1);
            titleRow.createCell(0).setCellValue("一级标题");
            titleRow.createCell(1).setCellValue("具体内容");
            titleRow.createCell(2).setCellValue("选项");
            int rowIndex = 2;

            //循环题目
            //获取这个用户 这个评审  说选的选项
            QueryWrapper<ClazzEvaluateOptionRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("evaluation", evaluationProcess.getId());
            wrapper.eq("user_id", userId);

            for (ClazzEvaluateOptionRecord clazzEvaluateOptionRecordTemp : clazzEvaluateOptionRecordMapper.selectList(wrapper)) {
                ClazzEvaluateOption clazzEvaluateOption = clazzEvaluateOptionMapper.selectByPrimaryKey(clazzEvaluateOptionRecordTemp.getEvaluationOptionId());
                Row row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue(clazzEvaluateOption.getFirstTarget());
                row.createCell(1).setCellValue(clazzEvaluateOption.getDetail());
                row.createCell(2).setCellValue(clazzEvaluateOptionRecordTemp.getMark());
                rowIndex++;
            }
            rowIndex++;

            //处理专家意见
            Row opinionRow = sheet.createRow(rowIndex);
            opinionRow.createCell(0).setCellValue("课程的突出优点:");
            opinionRow.createCell(1).setCellValue(clazzOpinionRecord.getClazzAdvantage());
            rowIndex++;
            opinionRow = sheet.createRow(rowIndex);
            opinionRow.createCell(0).setCellValue("课程存在的主要问题:");
            opinionRow.createCell(1).setCellValue(clazzOpinionRecord.getClazzProblem());
            rowIndex++;
            opinionRow = sheet.createRow(rowIndex);
            opinionRow.createCell(0).setCellValue("改进意见建议:");
            opinionRow.createCell(1).setCellValue(clazzOpinionRecord.getClazzAdvice());
            //设置列宽度
            sheet.setColumnWidth(0, 25 * 256);
            sheet.setColumnWidth(1, 175 * 256);
            sheet.setColumnWidth(2, 20 * 256);
        });
        return result;
    }


    @Override
    public Clazz getClazzById(Integer clazzId) {
        return clazzMapper.selectById(clazzId);
    }

    @Override
    public List<ClazzVO> getClazzByUserType(User user, Integer pageNum, Integer pageSize) {
        List<String> userType = UserUtil.getUserType(user);
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        if (userType.contains(ENGINEERING_EXPERT_LEADER) || userType.contains(ENGINEERING_EXPERT)) {
            queryWrapper.eq("type", ENGINEERING);
        } else if (userType.contains(HUMANITIES_AND_SOCIAL_SCIENCES_EXPERT) || userType.contains(HUMANITIES_AND_SOCIAL_SCIENCES_EXPERT_LEADER)) {
            queryWrapper.eq("type", HUMANITIES_AND_SOCIAL_SCIENCES);
        } else if (userType.contains(ARTS_AND_SCIENCES_EXPERT) || userType.contains(ARTS_AND_SCIENCES_EXPERT_LEADER)) {
            queryWrapper.eq("type", ARTS_AND_SCIENCES);
        }
        List<Clazz> selectList = clazzMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper).getRecords();
        return selectList.stream().map(clazz -> {
            ClazzVO clazzVO = new ClazzVO();
            BeanUtils.copyProperties(clazz, clazzVO);
            clazzVO.setUserClazzVO(new UserClazzVO(clazz.getUserId(), userMapper.selectByPrimaryKey(clazz.getUserId()).getRealName()));
            return clazzVO;
        }).collect(Collectors.toList());
    }
}
