package com.vtmer.microteachingquality.service;


import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.User;

import java.util.List;

public interface MajorArchiveService {


    /**
     * 归档专家 获取所有专业归档负责人信息，以创建审批信息
     *
     * @return 所有专业归档负责人信息
     * @author 墨小小
     */
    List<MajorArchiveGetManagerInfoResult> getManagerInfo();


    /**
     * 专业归档 添加新评审批次
     *
     * @param managerId 添加新的归档批次的负责人id
     * @param batchName 添加的新批次名
     * @return 添加的结果是否成功
     * @author 墨小小
     */
    boolean newBatch(Integer managerId, String batchName);


    /**
     * 归档专家 提交文件记录
     *
     * @param user      提交文件的用户
     * @param batchName 批次名
     * @param fileName  文件名
     * @param path      文件加密路径
     * @return insert的数据条数
     * @author 墨小小
     */
    int uploadFileRecord(User user, String batchName, String fileName, String path);

    /**
     * 专业归档 删除已经提交的文件
     *
     * @param user 提交文件的用户
     * @param path 文件加密路径
     * @return 删除的数据库条目数
     * @anthor 墨小小
     */
    int deleteUploadedFileRecord(User user, String path);

    /**
     * 专业归档 查看自己上传的所有文件信息
     *
     * @return 专业归档用户上传的所有文件信息批次
     * @author 墨小小
     */
    List<MajorArchiveGetUploadedFilesInfoResult> getUploadedFilesInfo();


    /**
     * 专业归档 获取模板文件信息
     *
     * @return 模板文件信息
     * @author 墨小小
     */
    List<MajorArchiveGetTemplateFilesInfoResult> getTemplateFilesInfo();


    /**
     * 专业归档负责人 上传模板文件记录
     *
     * @param user     上传文件的用户
     * @param fileName 文件的名字
     * @param path     文件的加密路径
     * @return insert的数据条数
     * @author 墨小小
     */
    int uploadTemplateFileRecord(User user, String fileName, String path);


    /**
     * 专业归档负责人 删除上传的模板文件记录
     *
     * @param fileName 文件名字
     * @param path     文件路径
     * @return
     * @author 墨小小
     */
    int deleteTemplateFileRecord(String fileName, String path);


    /**
     * 专业归档负责人 提交评审记录
     *
     * @param opinion   负责人意见
     * @param batchName 评审批次名字
     * @param majorName 专业名字
     * @return 提交结果
     * @author 墨小小
     */
    boolean submitEvaluation(String opinion, String batchName, String majorName);


    /**
     * 专业归档负责人 获取自己负责人专业与评审批次信息
     *
     * @return 获取自己负责人专业与评审批次信息
     * @author 墨小小
     */
    List<MajorArchiveGetManageMajorAndBatchInfoResult> getManageMajorAndBatchInfo();


    /**
     * 专业归档负责人 获取评审的文件信息
     *
     * @param batchName 批次名
     * @param majorId   专业id
     * @return 对应批次的文件信息
     * @author 墨小小
     */
    List<MajorArchiveGetBatchFilesInfoResult> getBatchFilesInfo(String batchName, Integer majorId);

    /**
     * 专业归档负责人 根据专业和批次获取已经评审的信息opinion
     *
     * @param batchName 评审批次名
     * @param majorId   专业id
     * @return 评审信息
     * @author 墨小小
     */
    MajorArchiveGetEvaluatedBatchInfoResult getEvaluatedBatchInfo(String batchName, Integer majorId);
}
