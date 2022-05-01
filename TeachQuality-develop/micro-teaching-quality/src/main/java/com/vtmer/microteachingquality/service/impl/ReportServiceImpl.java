package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.constant.enums.EvaluationStatus;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.common.exception.report.*;
import com.vtmer.microteachingquality.mapper.*;
import com.vtmer.microteachingquality.model.dto.insert.LeaderEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.insert.MasterEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.dto.select.GetAllMajorsDTO;
import com.vtmer.microteachingquality.model.dto.select.MajorDTO;
import com.vtmer.microteachingquality.model.entity.*;
import com.vtmer.microteachingquality.service.ReportService;
import com.vtmer.microteachingquality.service.UserService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hung.microoauth2commons.commonutils.utils.RedisConstants.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService {

    /**
     * 随机生成存储加密文件名(自评报告)的密钥
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    private Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    private UserService userService;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private ReportTemplateMapper reportTemplateMapper;
    @Autowired
    private EvaluationOptionMapper evaluationOptionMapper;
    @Autowired
    private OptionRecordMapper optionRecordMapper;
    @Autowired
    private MasterEvaluationMapper masterEvaluationMapper;
    @Autowired
    private LeaderEvaluationMapper leaderEvaluationMapper;
    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private LeaderInfoMapper leaderInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ManageInfoMapper manageInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 构建加密
     */
    private AES aes = SecureUtil.aes(ENCRYPT_KEY);

    @Value("${report.path}")
    private String reportPath;

    @Override
    public int saveReportTemplate(ReportTemplate reportTemplate) {
        reportTemplateMapper.deleteByMajor(reportTemplate.getMajor());
        return reportTemplateMapper.insertReportTemplate(reportTemplate);
    }

    @Override
    public int saveReportRecord(Report report) {
        reportMapper.deleteByMajor(report.getMajor());
        return reportMapper.insertReport(report);
    }

    @Override
    public ReportTemplate getReportTemplateByMajor(String major) {
        return reportTemplateMapper.selectByMajor(major);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReportRecord(User loginUser, Report report) {
        Report reportRecord = reportMapper.selectByMajor(loginUser.getUserBelong());
        if (ObjectUtil.isNull(reportRecord)) {
            throw new ReportTemplateNotExistException();
        }
        Integer reportId = reportRecord.getId();
        report.setId(reportId);
        return reportMapper.updateReportById(report);
    }

    @Override
    public List<Report> listReport() {
        List<Report> reportList = reportMapper.selectAll();
        return ObjectUtil.isNotNull(reportList) ? reportList : new ArrayList<>();
    }

    @Override
    public LeaderGetReportResult getReportByMajor(String major) {
        Report report = reportMapper.selectByMajor(major);
        LeaderGetReportResult result = new LeaderGetReportResult();
        BeanUtils.copyProperties(report, result);
        String filePath = reportPath + File.separator + aes.decryptStr(report.getPath(), CharsetUtil.CHARSET_UTF_8);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        result.setFileName(fileName);
        return result;
    }

    @Override
    public Report getReportById(Integer reportId) {
        return reportMapper.selectByPrimaryKey(reportId);
    }

    @Override
    public Report getReportByPath(String path) {
        return reportMapper.selectByPath(path);
    }

    @Override
    public List<OptionResult> listOption() {
        List<OptionResult> optionResultList = new ArrayList<>();
        List<EvaluationOption> evaluationOptionList = evaluationOptionMapper.selectAll();
        List<String> firstTargetList = new ArrayList<>();
        for (EvaluationOption evaluationOption : evaluationOptionList) {
            String firstTarget = evaluationOption.getFirstTarget();
            if (!CollUtil.contains(firstTargetList, firstTarget)) {
                firstTargetList.add(firstTarget);
                List<EvaluationOption> detailsListTemp = evaluationOptionMapper.selectByFirstTarget(firstTarget);
                Map<Integer, String> detailsMap = new HashMap<>();
                for (EvaluationOption details : detailsListTemp) {
                    detailsMap.put(details.getId(), details.getDetails());
                }
                OptionResult optionResult = new OptionResult();
                optionResult.setFirstTarget(firstTarget);
                optionResult.setDetails(detailsMap);
                optionResultList.add(optionResult);
            }
        }
        return optionResultList;
    }

    @Override
    public List<OptionResult> listOptionByType(String majorName) {
        String type = majorMapper.selectByMajorName(majorName).getType();
        if (StrUtil.isEmpty(type)) {
            return null;
        }
        List<OptionResult> optionResultList = new ArrayList<>();
        List<EvaluationOption> evaluationOptionList = evaluationOptionMapper.selectByCollageSort(type);
        if (CollUtil.isEmpty(evaluationOptionList)) {
            return null;
        }
        Map<String, OptionResult> optionResultMap = new HashMap<>();
        for (EvaluationOption evaluationOption : evaluationOptionList) {
            String firstTarget = evaluationOption.getFirstTarget();
            OptionResult optionResult = optionResultMap.get(firstTarget);

            if (optionResult != null) {
                //已经有这个一级指标
                optionResult.getDetails().put(evaluationOption.getId(), evaluationOption.getDetails());
            } else {
                //还没有这个一级指标
                optionResult = new OptionResult();
                Map<Integer, String> detailMap = new HashMap<>();

                optionResult.setFirstTarget(firstTarget);
                detailMap.put(evaluationOption.getId(), evaluationOption.getDetails());
                optionResult.setDetails(detailMap);

                optionResultMap.put(firstTarget, optionResult);
                optionResultList.add(optionResult);
            }
        }

        return optionResultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveMasterEvaluation(User loginUser, String majorName, MasterEvaluateDTO masterEvaluateDTO) {
        Integer userId = loginUser.getId();
        //获取被评审的专业信息
        Major major = majorMapper.selectByMajorName(majorName);
        // 检测是否已填写
        List<MasterEvaluation> evaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(userId, major.getId());
        if (CollUtil.isNotEmpty(evaluationList)) {
            MasterEvaluation evaluation = evaluationList.get(0);
            if (!evaluation.getStatus().equals(EvaluationStatus.WAITING_FILL.getStatus())) {
                throw new ReportEvaluationExistException();
            }
        }
        Map<Integer, String> optionMap = masterEvaluateDTO.getOptionMap();
        // 获取当前时间作为选项记录插入时间
        // 设置东八区时区
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        Date date = new Date();
        for (Map.Entry<Integer, String> option : optionMap.entrySet()) {
            OptionRecord optionRecord = new OptionRecord();
            optionRecord.setUserId(userId);
            optionRecord.setMajorId(major.getId());
            optionRecord.setOptionId(option.getKey());
            optionRecord.setMark(option.getValue());
            optionRecord.setCreateTime(date);
            optionRecordMapper.insert(optionRecord);
        }
        MasterEvaluation masterEvaluation = new MasterEvaluation();
        masterEvaluation.setUserId(userId);
        masterEvaluation.setMajorId(major.getId());
        masterEvaluation.setStatus(EvaluationStatus.CHECKED.getStatus());
        masterEvaluation.setOpinion(masterEvaluateDTO.getOpinion());
        return masterEvaluationMapper.insert(masterEvaluation);
    }

    @Override
    public int saveLeaderEvaluation(Integer userId, LeaderEvaluateDTO leaderEvaluateDTO) {
        // 检测是否已填写
        Major major = majorMapper.selectByMajorName(leaderEvaluateDTO.getMajorName());
        List<LeaderEvaluation> evaluationList = leaderEvaluationMapper.selectByUserIdAndMajorId(userId, major.getId());
        if (CollUtil.isNotEmpty(evaluationList)) {
            throw new ReportEvaluationExistException();
        }
        Map<Integer, String> optionMap = leaderEvaluateDTO.getOptionMap();
        for (Map.Entry<Integer, String> option : optionMap.entrySet()) {
            OptionRecord optionRecord = new OptionRecord();
            optionRecord.setUserId(userId);
            optionRecord.setMajorId(major.getId());
            optionRecord.setOptionId(option.getKey());
            optionRecord.setMark(option.getValue());
            optionRecordMapper.insert(optionRecord);
        }
        Report report = reportMapper.selectByMajor(leaderEvaluateDTO.getMajorName());
        report.setStatus(EvaluationStatus.CHECKED.getStatus());
        reportMapper.updateReportById(report);
        LeaderEvaluation leaderEvaluation = new LeaderEvaluation();
        leaderEvaluation.setMajorId(major.getId());
        leaderEvaluation.setUserId(userId);
        leaderEvaluation.setOpinion(leaderEvaluateDTO.getOpinion());
        leaderEvaluation.setResult(leaderEvaluateDTO.getResult());
        return leaderEvaluationMapper.insert(leaderEvaluation);
    }

    @Override
    public List<MasterInfoResult> listMaster(User loginLeader) {
        Integer leaderId = loginLeader.getId();
        List<MasterInfoResult> masterInfoResultList = new ArrayList<>();
        List<LeaderInfo> leaderInfoList = leaderInfoMapper.selectByLeaderId(leaderId);
        if (leaderInfoList == null || leaderInfoList.isEmpty()) {
            return null;
        }
        for (LeaderInfo leaderInfoTemp : leaderInfoList) {
            MasterInfoResult result = new MasterInfoResult();
            User user = userMapper.selectByPrimaryKey(leaderInfoTemp.getMemberId());
            result.setMemberId(leaderInfoTemp.getMemberId());
            result.setMemberName(user.getRealName());
            List<MasterInfoMajorInfoResult> masterInfoMajorInfoResultList = new ArrayList<>();
            //获取下属负责的专业
            List<ManageInfo> manageInfoList = manageInfoMapper.selectByUserId(leaderInfoTemp.getMemberId());
            for (ManageInfo manageInfoTemp : manageInfoList) {
                MasterInfoMajorInfoResult infoResult = new MasterInfoMajorInfoResult();
                Major major = majorMapper.selectByPrimaryKey(manageInfoTemp.getMajorId());
                infoResult.setMajorName(major.getName());
                List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(leaderInfoTemp.getMemberId(), major.getId());
                if (masterEvaluationList == null || masterEvaluationList.isEmpty()) {
                    infoResult.setStatus("未查询到评审信息");
                } else {
                    MasterEvaluation masterEvaluation = masterEvaluationList.get(0);
                    infoResult.setStatus(masterEvaluation.getStatus());
                }
                infoResult.setMajorId(major.getId());
                masterInfoMajorInfoResultList.add(infoResult);
            }
            result.setMajorInfo(masterInfoMajorInfoResultList);
            masterInfoResultList.add(result);
        }


        return masterInfoResultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MasterEvaluationResult getMasterEvaluationByUserIdAndMajorName(Integer userId, String majorName, boolean isLeader) {
        MasterEvaluationResult masterEvaluationResult = new MasterEvaluationResult();
        Major major = majorMapper.selectByMajorName(majorName);
        //先获取专家意见
        List<MasterEvaluation> evaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(userId, major.getId());
        MasterEvaluation evaluation;
        if (CollUtil.isNotEmpty(evaluationList)) {
            evaluation = evaluationList.get(0);
        } else {
            evaluation = null;
        }

        List<OptionRecord> optionRecordList = optionRecordMapper.selectByUserIdAndMajorId(userId, major.getId());
        if (ObjectUtil.isNull(evaluation) || evaluation.getStatus().equals(EvaluationStatus.WAITING_FILL.getStatus()) || CollUtil.isEmpty(optionRecordList)) {
            // throw new ReportEvaluationNotExistException();
            return null;
        } else if (isLeader) {//是专家组长
            // 修改评估状态
            if (evaluation.getStatus().equals(EvaluationStatus.WAITING_FILL.getStatus())) {
                throw new ReportEvaluationStatusNotFItException();
            }
            //不更新时候看过的状态
            /*else if (evaluation.getStatus().equals(EvaluationStatus.NOT_CHECKED.getStatus())) {
                masterEvaluationMapper.updateStatusByUserIdAndMajorId(userId, major.getId(), EvaluationStatus.CHECKED.getStatus());
            }*/
        }
        masterEvaluationResult.setOpinion(evaluation.getOpinion());
        masterEvaluationResult.setStatus(evaluation.getStatus());
        Map<Integer, String> optionMap = new HashMap<>();
        for (OptionRecord optionRecord : optionRecordList) {
            optionMap.put(optionRecord.getOptionId(), optionRecord.getMark());
        }
        masterEvaluationResult.setOptionMap(optionMap);

        return masterEvaluationResult;
    }



    @Override
    public LeaderEvaluationResult getMasterEvaluationByUserIdAndMajorName(Integer userId, String majorName) {
        LeaderEvaluationResult result = new LeaderEvaluationResult();
        Major major = majorMapper.selectByMajorName(majorName);
        List<LeaderEvaluation> evaluationList = leaderEvaluationMapper.selectByUserIdAndMajorId(userId, major.getId());
        LeaderEvaluation evaluation;
        if (CollUtil.isNotEmpty(evaluationList)) {
            evaluation = evaluationList.get(0);
        } else {
            evaluation = null;
        }
        List<OptionRecord> optionRecordList = optionRecordMapper.selectByUserIdAndMajorId(userId, major.getId());
        if (ObjectUtil.isNull(evaluation) || CollUtil.isEmpty(optionRecordList)) {
            // throw new ReportEvaluationNotExistException();
            return null;
        }
        result.setOpinion(evaluation.getOpinion());
        result.setResult(evaluation.getResult());
        Map<Integer, String> optionMap = new HashMap<>();
        for (OptionRecord optionRecord : optionRecordList) {
            optionMap.put(optionRecord.getOptionId(), optionRecord.getMark());
        }
        result.setOptionMap(optionMap);
        return result;
    }

    @Override
    public int cancelEvaluation(Integer userId, String majorName) {
        Major major = majorMapper.selectByMajorName(majorName);
        List<MasterEvaluation> evaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(userId, major.getId());
        if (CollUtil.isEmpty(evaluationList)) {
            throw new ReportEvaluationNotExistException();
        }
        if (!evaluationList.get(0).getStatus().equals(EvaluationStatus.CHECKED.getStatus())) {
            throw new ReportEvaluatoinCancelException();
        }
        return masterEvaluationMapper.updateStatusByUserIdAndMajorId(userId, major.getId(), EvaluationStatus.WAITING_FILL.getStatus());
    }

    @Override
    public int updateReportResult(String major, String result) {
        return reportMapper.updateReportByMajor(major, result);
    }

    @Override
    public List<GetAllMajorsAndTypesResult> getAllMajorsAndTypes(MajorDTO majorDTO) {
        if (ObjectUtil.isNull(majorDTO) || majorDTO.getPageIndex() == null || majorDTO.getPageLength() == null || majorDTO.getPageLength() < 0 || majorDTO.getPageIndex() < 0) {
            return null;
        }
        List<Major> majorList = majorMapper.selectAllMajors((majorDTO.getPageIndex() - 1) * majorDTO.getPageLength(), majorDTO.getPageLength());
        List<GetAllMajorsAndTypesResult> resultList = new ArrayList<>();
        Map<String, GetAllMajorsAndTypesResult> map = new HashMap<>();
        for (Major major : majorList) {
            GetAllMajorsAndTypesResult getAllMajorsAndTypesResult = map.get(major.getType());
            if (getAllMajorsAndTypesResult != null) {
                GetAllMajorsResult majorsAndTypesDTO = new GetAllMajorsResult();
                majorsAndTypesDTO.setCollege(major.getCollege());
                majorsAndTypesDTO.setName(major.getName());
                getAllMajorsAndTypesResult.getMajors().add(majorsAndTypesDTO);
            } else {
                getAllMajorsAndTypesResult = new GetAllMajorsAndTypesResult();
                getAllMajorsAndTypesResult.setMajorType(major.getType());
                getAllMajorsAndTypesResult.setMajors(new ArrayList<GetAllMajorsResult>());
                GetAllMajorsResult majorsAndTypesDTO = new GetAllMajorsResult();
                majorsAndTypesDTO.setCollege(major.getCollege());
                majorsAndTypesDTO.setName(major.getName());
                getAllMajorsAndTypesResult.getMajors().add(majorsAndTypesDTO);
                map.put(major.getType(), getAllMajorsAndTypesResult);
                resultList.add(getAllMajorsAndTypesResult);
            }
        }
        return resultList;
    }

    @Override
    public GetAllMajorsDTO getAllMajors(MajorDTO majorDTO) {
        if (ObjectUtil.isNull(majorDTO) || majorDTO.getPageIndex() == null || majorDTO.getPageLength() == null || majorDTO.getPageLength() < 0 || majorDTO.getPageIndex() < 0) {
            return null;
        }
        GetAllMajorsDTO result = new GetAllMajorsDTO();
        List<Major> majorList = majorMapper.selectAllMajors((majorDTO.getPageIndex() - 1) * majorDTO.getPageLength(), majorDTO.getPageLength());
        List<GetAllMajorsResult> resultList = new ArrayList<>();

        for (Major major : majorList) {
            GetAllMajorsResult majorsResult = new GetAllMajorsResult();
            majorsResult.setName(major.getName());
            majorsResult.setCollege(major.getCollege());
            ReportTemplate reportTemplate = reportTemplateMapper.selectByMajor(major.getName());
            if (reportTemplate != null) {
                String fileName = StrUtil.subAfter(reportTemplate.getPath(), "/", true);
                majorsResult.setFileName(fileName);
                majorsResult.setFilePath(reportTemplate.getPath());
            } else {
                majorsResult.setFilePath(null);
                majorsResult.setFileName(null);
            }
            resultList.add(majorsResult);
        }
        result.setMajorsList(resultList);
        result.setSize(majorMapper.selectCounts());
        return result;
    }

    @Override
    public List<ExpertGetMajorInfoResult> getMajorInfo() {
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        List<ManageInfo> manageInfoList = manageInfoMapper.selectByUserId(user.getId());
        List<ExpertGetMajorInfoResult> resultList = new ArrayList<>();
        for (ManageInfo manageInfoTemp : manageInfoList) {
            ExpertGetMajorInfoResult result = new ExpertGetMajorInfoResult();
            result.setMajorId(manageInfoTemp.getMajorId());
            Major major = majorMapper.selectByPrimaryKey(manageInfoTemp.getMajorId());
            result.setMajorName(major.getName());
            result.setMajorCollege(major.getCollege());
            Report report = reportMapper.selectByMajor(major.getName());
            result.setReportPath(report.getPath());

            //根据登录的用户类型在不同表获取status
            if (user.getUserType().equals(UserType.LEADER.getType())) {
                //组长 组长是跟着report的
                result.setStatus(report.getStatus());
            } else if (user.getUserType().equals(UserType.MASTER.getType())) {
                //组员
                List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(user.getId(), major.getId());
                if (masterEvaluationList == null | masterEvaluationList.isEmpty()) {
                    result.setStatus("未查询到评审信息");
                } else {
                    result.setStatus(masterEvaluationList.get(0).getStatus());
                }
            } else {
                //没查询到身份
                result.setStatus("未查询到评审信息");
            }
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public GetFileInfoResult getFileInfo(String majorName) {
        GetFileInfoResult result = new GetFileInfoResult();
        Report report = reportMapper.selectByMajor(majorName);
        result.setFilePath(report.getPath());
        result.setReportId(report.getId());
        result.setUpdateTime(report.getUpdateTime());
        User user = userMapper.selectByPrimaryKey(report.getUserId());
        result.setUploaderId(user.getId());
        result.setUploaderRealName(user.getRealName());
        result.setUpLoaderBelongs(user.getUserBelong());
        return result;
    }

    @Override
    public ExpertGetNotEvaluatedInfoResult getNotEvaluatedInfo() {
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        ExpertGetNotEvaluatedInfoResult result = new ExpertGetNotEvaluatedInfoResult();
        List<ExpertGetNotEvaluatedInfoMajorsInfoResult> infoResultList = new ArrayList<>();
        List<ManageInfo> manageInfoList = manageInfoMapper.selectByUserId(user.getId());
        int counts = 0;
        for (ManageInfo manageInfoTemp : manageInfoList) {
            Major major = majorMapper.selectByPrimaryKey(manageInfoTemp.getMajorId());
            if (user.getUserType().equals(UserType.MASTER.getType())) {
                //组员
                List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(user.getId(), major.getId());
                if (masterEvaluationList == null || masterEvaluationList.isEmpty()) {
                    //找不到说明还没评审
                    ExpertGetNotEvaluatedInfoMajorsInfoResult resultTemp = new ExpertGetNotEvaluatedInfoMajorsInfoResult();
                    resultTemp.setMajorCollege(major.getCollege());
                    resultTemp.setMajorName(major.getName());
                    resultTemp.setMajorType(major.getType());
                    resultTemp.setMajorId(major.getId());
                    resultTemp.setStatus(EvaluationStatus.NOT_CHECKED.getStatus());
                    infoResultList.add(resultTemp);
                    counts++;
                } else {
                    continue;
                }
            } else if (user.getUserType().equals(UserType.LEADER.getType())) {
                //组长
                Report report = reportMapper.selectByMajor(major.getName());
                if (report.getStatus().equals(EvaluationStatus.NOT_CHECKED.getStatus()) ||
                        report.getStatus().equals(EvaluationStatus.WAITING_FILL.getStatus())) {
                    //未审阅
                    ExpertGetNotEvaluatedInfoMajorsInfoResult resultTemp = new ExpertGetNotEvaluatedInfoMajorsInfoResult();
                    resultTemp.setMajorCollege(major.getCollege());
                    resultTemp.setMajorName(major.getName());
                    resultTemp.setMajorType(major.getType());
                    resultTemp.setMajorId(major.getId());
                    resultTemp.setStatus(report.getStatus());
                    infoResultList.add(resultTemp);
                    counts++;
                }
            }
        }
        result.setMajorsInfo(infoResultList);
        result.setTotalCounts(counts);
        return result;
    }

    @Override
    public ExpertGetEvaluatedInfoResult getEvaluatedInfo() {
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        ExpertGetEvaluatedInfoResult result = new ExpertGetEvaluatedInfoResult();
        List<ExpertGetEvaluatedInfoMajorsInfoResult> resultList = new ArrayList<>();
        int counts = 0;
        List<ManageInfo> manageInfoList = manageInfoMapper.selectByUserId(user.getId());
        for (ManageInfo manageInfoTemp : manageInfoList) {
            Major major = majorMapper.selectByPrimaryKey(manageInfoTemp.getMajorId());
            if (user.getUserType().equals(UserType.MASTER.getType())) {
                //组员
                List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectByUserIdAndMajorId(user.getId(), major.getId());
                if (masterEvaluationList == null || masterEvaluationList.isEmpty()) {
                    //找不到说明还没评审，找到了就说明评审了
                    continue;
                } else {
                    MasterEvaluation masterEvaluation = masterEvaluationList.get(0);
                    ExpertGetEvaluatedInfoMajorsInfoResult resultTemp = new ExpertGetEvaluatedInfoMajorsInfoResult();
                    resultTemp.setStauts(masterEvaluation.getStatus());
                    resultTemp.setMajorId(major.getId());
                    resultTemp.setMajorName(major.getName());
                    resultTemp.setMajorType(major.getType());
                    resultTemp.setMajorCollege(major.getCollege());
                    resultList.add(resultTemp);
                }
            } else if (user.getUserType().equals(UserType.LEADER.getType())) {
                //组长
                Report report = reportMapper.selectByMajor(major.getName());
                if (report.getStatus().equals(EvaluationStatus.WAITING_FILL.getStatus()) ||
                        report.getStatus().equals(EvaluationStatus.NOT_CHECKED.getStatus())) {
                    //未审阅或者是未填写
                    continue;
                } else {
                    //审阅了
                    ExpertGetEvaluatedInfoMajorsInfoResult resultTemp = new ExpertGetEvaluatedInfoMajorsInfoResult();
                    resultTemp.setStauts(report.getStatus());
                    resultTemp.setMajorId(major.getId());
                    resultTemp.setMajorName(major.getName());
                    resultTemp.setMajorType(major.getType());
                    resultTemp.setMajorCollege(major.getCollege());
                    resultList.add(resultTemp);
                }
            }
        }
        result.setTotalCounts(counts);
        result.setMajorsInfo(resultList);
        return result;
    }

    @Override
    public XSSFWorkbook exportRecord(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (UserType.MASTER.getType().equals(user.getUserType())) {
            //组员
            List<MasterEvaluation> masterEvaluationList = masterEvaluationMapper.selectByUserId(userId);
            //每条记录代表对一个专业进行评审
            for (MasterEvaluation masterEvaluation : masterEvaluationList) {
                Major major = majorMapper.selectByPrimaryKey(masterEvaluation.getMajorId());
                List<OptionRecord> optionRecordList = optionRecordMapper.selectByUserIdAndMajorId(userId, masterEvaluation.getMajorId());

                Sheet sheet = workbook.createSheet(major.getCollege() + "-" + major.getName());
                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("专业类型：" + major.getType());
                titleRow.createCell(1).setCellValue("所属学院：" + major.getCollege());
                titleRow.createCell(2).setCellValue("专业名字：" + major.getName());
                titleRow.createCell(3).setCellValue("评审时间：" + masterEvaluation.getUpdateTime());

                titleRow = sheet.createRow(1);
                titleRow.createCell(0).setCellValue("一级标题");
                titleRow.createCell(1).setCellValue("具体内容");
                titleRow.createCell(2).setCellValue("选项");
                int rowIndex = 2;
                for (OptionRecord optionRecord : optionRecordList) {
                    EvaluationOption evaluationOption = evaluationOptionMapper.selectByPrimaryKey(optionRecord.getOptionId());
                    Row row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(evaluationOption.getFirstTarget());
                    row.createCell(1).setCellValue(evaluationOption.getDetails());
                    row.createCell(2).setCellValue(optionRecord.getMark());
                    rowIndex++;
                }
                rowIndex++;
                //处理专家意见
                Row opinionRow = sheet.createRow(rowIndex);
                opinionRow.createCell(0).setCellValue("专家意见：");
                opinionRow.createCell(1).setCellValue(masterEvaluation.getOpinion());
                rowIndex++;

                //设置列宽度
                sheet.setColumnWidth(0, 25 * 256);
                sheet.setColumnWidth(1, 175 * 256);
                sheet.setColumnWidth(2, 20 * 256);

            }
        } else if (UserType.LEADER.getType().equals(user.getUserType())) {
            //组长
            List<LeaderEvaluation> leaderEvaluationList = leaderEvaluationMapper.selectByUserId(userId);
            //每条记录代表对一个专业进行评审
            for (LeaderEvaluation leaderEvaluation : leaderEvaluationList) {
                Major major = majorMapper.selectByPrimaryKey(leaderEvaluation.getMajorId());
                List<OptionRecord> optionRecordList = optionRecordMapper.selectByUserIdAndMajorId(userId, leaderEvaluation.getMajorId());

                Sheet sheet = workbook.createSheet(major.getCollege() + "-" + major.getName());
                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("专业类型：" + major.getType());
                titleRow.createCell(1).setCellValue("所属学院：" + major.getCollege());
                titleRow.createCell(2).setCellValue("专业名字：" + major.getName());
                titleRow.createCell(3).setCellValue("评审时间：" + leaderEvaluation.getUpdateTime());

                titleRow = sheet.createRow(1);
                titleRow.createCell(0).setCellValue("一级标题");
                titleRow.createCell(1).setCellValue("具体内容");
                titleRow.createCell(2).setCellValue("选项");
                int rowIndex = 2;
                for (OptionRecord optionRecord : optionRecordList) {
                    EvaluationOption evaluationOption = evaluationOptionMapper.selectByPrimaryKey(optionRecord.getOptionId());
                    Row row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(evaluationOption.getFirstTarget());
                    row.createCell(1).setCellValue(evaluationOption.getDetails());
                    row.createCell(2).setCellValue(optionRecord.getMark());
                    rowIndex++;
                }
                rowIndex++;
                //处理专家意见
                Row opinionRow = sheet.createRow(rowIndex);
                opinionRow.createCell(0).setCellValue("专家意见：");
                opinionRow.createCell(1).setCellValue(leaderEvaluation.getOpinion());
                rowIndex++;
                opinionRow = sheet.createRow(rowIndex);
                opinionRow.createCell(0).setCellValue("评审结果：");
                opinionRow.createCell(1).setCellValue(leaderEvaluation.getResult());
                //设置列宽度
                sheet.setColumnWidth(0, 25 * 256);
                sheet.setColumnWidth(1, 175 * 256);
                sheet.setColumnWidth(2, 20 * 256);
            }
        }
        return workbook;
    }

    @Override
    public MasterEvaluationResult getMasterEvaluationByUserIdAndMajorNameWithCache(
            Integer userId, String majorName, boolean isLeader
    ) {
        String key = CACHE_NODE_KEY + userId  + majorName;
        // 1.从redis查询缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(shopJson, MasterEvaluationResult.class);
        }
        // 判断命中的是否是空值
        if (shopJson != null) {
            // 返回一个错误信息
            return null;
        }

        // 4.实现缓存重建
        // 4.1.获取分布式锁
        String lockKey = LOCK_NODE_KEY  + userId  + majorName;
        MasterEvaluationResult r = null;
        RLock lock=null;
        try {
            lock=redissonClient.getLock(lockKey);
            /**
             * 尝试获取锁，对应的参数：获取锁的最大等待时间。锁自动释放时间，时间单位
             */
            boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
            // 4.2.判断是否获取成功
            if (!isLock) {
                // 4.3.获取锁失败，休眠并重试
                Thread.sleep(50);
                return getMasterEvaluationByUserIdAndMajorNameWithCache(userId,majorName,isLeader);
            }
            // 4.4.获取锁成功，根据t查询数据库
            r = getMasterEvaluationByUserIdAndMajorName(userId,majorName,isLeader);
            // 5.不存在，返回错误
            if (r == null) {
                // 将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                // 返回错误信息
                return null;
            }
            // 6.存在，写入redis
            stringRedisTemplate.opsForValue().set(key, String.valueOf(r), CACHE_NULL_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 7.释放锁
            assert lock != null;
            lock.unlock();
        }
        // 8.返回
        return r;
    }

}
