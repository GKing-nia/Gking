package com.vtmer.microteachingquality.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.exception.CommonRuntimeException;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.MajorArchiveService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class MajorArchiveServiceImpl implements MajorArchiveService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MajorMapper majorMapper;
    @Resource
    private MajorArchiveFileMapper majorArchiveFileMapper;
    @Resource
    private MajorArchiveTemplateFileMapper majorArchiveTemplateFileMapper;
    @Resource
    private MajorArchiveManageInfoMapper majorArchiveManageInfoMapper;
    @Resource
    private MajorArchiveOpinionMapper majorArchiveOpinionMapper;

    /**
     * 归档专家 获取所有专业归档负责人信息，以创建审批信息
     *
     * @return 所有专业归档负责人信息
     * @author 墨小小
     */
    @Override
    public List<MajorArchiveGetManagerInfoResult> getManagerInfo() {
        //(在user表根据userType获取)
        List<User> userList = userMapper.selectByUserType("专业归档负责人");
        List<MajorArchiveGetManagerInfoResult> resultList = new ArrayList<>();
        for (User userTemp : userList) {
            MajorArchiveGetManagerInfoResult result = new MajorArchiveGetManagerInfoResult();
            result.setRealName(userTemp.getRealName());
            result.setUserBelong(userTemp.getUserBelong());
            result.setId(userTemp.getId());
            resultList.add(result);
        }
        return resultList;
    }


    /**
     * 专业归档 添加新评审批次
     *
     * @param managerId 添加新的归档批次的负责人id
     * @param batchName 添加的新批次名
     * @return 添加的结果是否成功
     * @author 墨小小
     */
    @Override
    public boolean newBatch(Integer managerId, String batchName) {
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Major major = majorMapper.selectByMajorName(user.getUserBelong());
        MajorArchiveManageInfo info = majorArchiveManageInfoMapper.selectByMajorIdAndBatchName(major.getId(), batchName);
        if (info != null) {
            throw new CommonRuntimeException("该评审批次已存在");
        }
        MajorArchiveManageInfo record = new MajorArchiveManageInfo();
        record.setBatchName(batchName);
        record.setUserId(managerId);
        record.setStatus("0");

        record.setMajorId(major.getId());
        return majorArchiveManageInfoMapper.insert(record) > 0;
    }

    /**
     * 归档专家 提交文件接口
     *
     * @param user      提交文件的用户
     * @param batchName 批次名
     * @param fileName  文件名
     * @param path      文件加密路径
     * @return insert的数据条数
     * @author 墨小小
     */
    @Override
    public int uploadFileRecord(User user, String batchName, String fileName, String path) {
        MajorArchiveFile majorArchiveFile = new MajorArchiveFile();
        majorArchiveFile.setUserId(user.getId());
        Major major = majorMapper.selectByMajorName(user.getUserBelong());
        if (major != null) {
            majorArchiveFile.setMajorId(major.getId());
        }
        majorArchiveFile.setFileName(fileName);
        majorArchiveFile.setPath(path);
        majorArchiveFile.setCreateTime(new Date());
        majorArchiveFile.setUpdateTime(new Date());
        majorArchiveFile.setBatchName(batchName);
        return majorArchiveFileMapper.insert(majorArchiveFile);
    }

    /**
     * 专业归档 删除已经提交的文件
     *
     * @param user 提交文件的用户
     * @param path 文件加密路径
     * @return 删除的数据库条目数
     * @anthor 墨小小
     */
    @Override
    public int deleteUploadedFileRecord(User user, String path) {
        return majorArchiveFileMapper.deleteByUserIdAndPath(user.getId(), path);
    }

    /**
     * 专业归档 查看自己上传的所有文件信息
     *
     * @return 专业归档用户上传的所有文件信息批次
     * @author 墨小小
     */
    @Override
    public List<MajorArchiveGetUploadedFilesInfoResult> getUploadedFilesInfo() {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Major userMajor = majorMapper.selectByMajorName(loginUser.getUserBelong());
        //获取评审批次信息
        List<MajorArchiveManageInfo> majorArchiveManageInfoList = majorArchiveManageInfoMapper.selectByMajorId(userMajor.getId());
        if (majorArchiveManageInfoList == null || majorArchiveManageInfoList.isEmpty()) {
            return null;
        }
        //获取上传的文件信息
        List<MajorArchiveFile> majorArchiveFileList = majorArchiveFileMapper.selectByUserId(loginUser.getId());
        List<MajorArchiveGetUploadedFilesInfoResult> resultList = new ArrayList<>();
        Map<String, MajorArchiveGetUploadedFilesInfoResult> resultMap = new HashMap<>();
        //先添加Result
        for (MajorArchiveManageInfo infoTemp : majorArchiveManageInfoList) {
            MajorArchiveGetUploadedFilesInfoResult result = new MajorArchiveGetUploadedFilesInfoResult();
            result.setFileInfo(new ArrayList<>());
            result.setStatus(infoTemp.getStatus());
            result.setBatchName(infoTemp.getBatchName());
            resultMap.put(infoTemp.getBatchName(), result);
            resultList.add(result);
        }
        //再添加FileInfoList
        for (MajorArchiveFile fileTemp : majorArchiveFileList) {
            List<MajorArchiveGetUploadedFilesInfoFileListResult> fileListResultList = resultMap.get(fileTemp.getBatchName()).getFileInfo();
            MajorArchiveGetUploadedFilesInfoFileListResult fileListResult = new MajorArchiveGetUploadedFilesInfoFileListResult();
            fileListResult.setUpdateTime(fileTemp.getUpdateTime());
            fileListResult.setPath(fileTemp.getPath());
            fileListResult.setId(fileTemp.getId());
            fileListResult.setFileName(fileTemp.getFileName());
            fileListResultList.add(fileListResult);
        }
        return resultList;
    }


    /**
     * 专业归档 获取模板文件信息
     *
     * @return 模板文件信息
     * @author 墨小小
     */
    @Override
    public List<MajorArchiveGetTemplateFilesInfoResult> getTemplateFilesInfo() {
        List<MajorArchiveGetTemplateFilesInfoResult> resultList = new ArrayList<>();
        List<MajorArchiveTemplateFile> majorArchiveTemplateFileList = majorArchiveTemplateFileMapper.selectAll();
        for (MajorArchiveTemplateFile temp : majorArchiveTemplateFileList) {
            MajorArchiveGetTemplateFilesInfoResult result = new MajorArchiveGetTemplateFilesInfoResult();
            result.setId(temp.getId());
            result.setFilePath(temp.getPath());
            result.setFileName(temp.getFileName());
            result.setUpdateTime(temp.getUpdateTime());
            resultList.add(result);
        }
        return resultList;
    }


    /**
     * 专业归档负责人 上传模板文件记录
     *
     * @param user     上传文件的用户
     * @param fileName 文件的名字
     * @param path     文件的加密路径
     * @return insert的数据条数
     * @author 墨小小
     */
    @Override
    public int uploadTemplateFileRecord(User user, String fileName, String path) {
        MajorArchiveTemplateFile record = new MajorArchiveTemplateFile();
        record.setUpdateTime(new Date());
        record.setCreateTime(new Date());
        record.setUserId(user.getId());
        record.setFileName(fileName);
        record.setPath(path);
        return majorArchiveTemplateFileMapper.insert(record);
    }

    /**
     * 专业归档负责人 删除上传的模板文件记录
     *
     * @param fileName 文件名字
     * @param path     文件路径
     * @return
     * @author 墨小小
     */
    @Override
    public int deleteTemplateFileRecord(String fileName, String path) {
        return majorArchiveTemplateFileMapper.deleteByFileNameAndPath(fileName, path);
    }

    /**
     * 专业归档负责人 提交评审记录
     *
     * @param opinion   负责人意见
     * @param batchName 评审批次名字
     * @param majorName 专业名字
     * @return 提交结果
     * @author 墨小小
     */
    @Override
    public boolean submitEvaluation(String opinion, String batchName, String majorName) {
        if (opinion == null || "".equals(opinion) || batchName == null || "".equals(batchName) || majorName == null || "".equals(majorName)) {
            throw new CommonRuntimeException("参数不能为空");
        }
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        Major major = majorMapper.selectByMajorName(majorName);
        if (major == null) {
            throw new CommonRuntimeException("查询不到该专业");
        }
        MajorArchiveManageInfo infoRecord = majorArchiveManageInfoMapper.selectByMajorIdAndBatchName(major.getId(), batchName);
        if (infoRecord == null) {
            throw new CommonRuntimeException("查询不到该评审批次");
        } else if (infoRecord.getStatus().equals("1")) {
            throw new CommonRuntimeException("该评审批次已被评审");
        }
        infoRecord.setStatus("1");
        MajorArchiveOpinion record = new MajorArchiveOpinion();
        record.setMajorId(major.getId());
        record.setOpinion(opinion);
        record.setBatchName(batchName);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setUserId(user.getId());
        return majorArchiveOpinionMapper.insert(record) > 0 && majorArchiveManageInfoMapper.updateByPrimaryKey(infoRecord) > 0;
    }

    /**
     * 专业归档负责人 获取自己负责人专业与评审批次信息
     *
     * @return 获取自己负责人专业与评审批次信息
     * @author 墨小小
     */
    @Override
    public List<MajorArchiveGetManageMajorAndBatchInfoResult> getManageMajorAndBatchInfo() {
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        List<MajorArchiveManageInfo> majorArchiveManageInfoList = majorArchiveManageInfoMapper.selectByUserId(user.getId());
        if (majorArchiveManageInfoList == null || majorArchiveManageInfoList.isEmpty()) {
            return null;
        }
        List<MajorArchiveGetManageMajorAndBatchInfoResult> resultList = new ArrayList<>();
        //majorId == Result
        Map<Integer, MajorArchiveGetManageMajorAndBatchInfoResult> resultMap = new HashMap<>();
        for (MajorArchiveManageInfo infoTemp : majorArchiveManageInfoList) {
            MajorArchiveGetManageMajorAndBatchInfoResult result = resultMap.get(infoTemp.getMajorId());
            Major major = majorMapper.selectByPrimaryKey(infoTemp.getMajorId());
            if (result != null) {
                MajorArchiveGetManageMajorAndBatchInfoBatchListResult resultListTemp = new MajorArchiveGetManageMajorAndBatchInfoBatchListResult();
                resultListTemp.setBatchName(infoTemp.getBatchName());
                resultListTemp.setStatus(infoTemp.getStatus());
                result.getBatchList().add(resultListTemp);
            } else {
                result = new MajorArchiveGetManageMajorAndBatchInfoResult();
                result.setMajorName(major.getName());
                result.setMajorId(major.getId());
                result.setBatchList(new ArrayList<>());
                MajorArchiveGetManageMajorAndBatchInfoBatchListResult resultListTemp = new MajorArchiveGetManageMajorAndBatchInfoBatchListResult();
                resultListTemp.setBatchName(infoTemp.getBatchName());
                resultListTemp.setStatus(infoTemp.getStatus());
                result.getBatchList().add(resultListTemp);
                resultList.add(result);
                resultMap.put(infoTemp.getMajorId(), result);
            }
        }
        return resultList;
    }


    /**
     * 专业归档负责人 获取评审的文件信息
     *
     * @param batchName 批次名
     * @param majorId   专业id
     * @return 对应批次的文件信息
     * @author 墨小小
     */
    @Override
    public List<MajorArchiveGetBatchFilesInfoResult> getBatchFilesInfo(String batchName, Integer majorId) {
        if (batchName == null || "".equals(batchName) || majorId == null) {
            throw new CommonRuntimeException("参数不能为空");
        }
        List<MajorArchiveFile> majorArchiveFileList = majorArchiveFileMapper.selectByBatchNameAndMajorId(batchName, majorId);
        if (majorArchiveFileList == null || majorArchiveFileList.isEmpty()) {
            return null;
        }
        List<MajorArchiveGetBatchFilesInfoResult> resultList = new ArrayList<>();
        for (MajorArchiveFile fileTemp : majorArchiveFileList) {
            MajorArchiveGetBatchFilesInfoResult result = new MajorArchiveGetBatchFilesInfoResult();
            result.setFileName(fileTemp.getFileName());
            result.setPath(fileTemp.getPath());
            result.setId(fileTemp.getId());
            result.setUpdateTime(fileTemp.getUpdateTime());
            result.setUserId(fileTemp.getUserId());
            User user = userMapper.selectByPrimaryKey(fileTemp.getUserId());
            result.setUserName(user.getRealName());
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 专业归档负责人 根据专业和批次获取已经评审的信息opinion
     *
     * @param batchName 评审批次名
     * @param majorId   专业id
     * @return 评审信息
     * @author 墨小小
     */
    @Override
    public MajorArchiveGetEvaluatedBatchInfoResult getEvaluatedBatchInfo(String batchName, Integer majorId) {
        if (batchName == null || "".equals(batchName) || majorId == null) {
            throw new CommonRuntimeException("参数不能为空");
        }
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        //判断评审
        MajorArchiveManageInfo majorArchiveManageInfo = majorArchiveManageInfoMapper.selectByMajorIdAndBatchName(majorId, batchName);
        if (majorArchiveManageInfo == null) {
            throw new CommonRuntimeException("未查询到评审批次信息");
        } else if ("0".equals(majorArchiveManageInfo.getStatus())) {
//            throw new CommonRuntimeException("尚未进行评审");
            return null;
        }
        MajorArchiveOpinion majorArchiveOpinion = majorArchiveOpinionMapper.selectByBatchNameAndMajorIdAndUserId(batchName, majorId, user.getId());
        if (majorArchiveOpinion == null) {
            throw new CommonRuntimeException("未查询到意见");
        }
        MajorArchiveGetEvaluatedBatchInfoResult result = new MajorArchiveGetEvaluatedBatchInfoResult();
        result.setId(majorArchiveOpinion.getId());
        result.setOpinion(majorArchiveOpinion.getOpinion());
        result.setUpdateTime(majorArchiveOpinion.getUpdateTime());
        return result;
    }
}
