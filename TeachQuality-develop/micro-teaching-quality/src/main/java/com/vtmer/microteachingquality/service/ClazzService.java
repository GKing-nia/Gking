package com.vtmer.microteachingquality.service;


import com.vtmer.microteachingquality.model.dto.result.ClazzVO;
import com.vtmer.microteachingquality.model.dto.result.GetAllClazzInfoResult;
import com.vtmer.microteachingquality.model.dto.result.GetEvaluationClazzByUserIdResult;
import com.vtmer.microteachingquality.model.entity.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ClazzService {

    /**
     * 获取所有的课程材料模版信息
     *
     * @return 返回一个list集合
     */
    List<ClazzAnnotation> getAnnotation();

    /**
     * 根据id查询课程报告材料模版信息
     *
     * @param id clazzAnnotation表的主键
     * @return 返回ClazzAnnotation对象
     */
    ClazzAnnotation getClazzAnnotationById(Integer id);

    /**
     * 获取所有的课程自评报告模版
     *
     * @return 返回一个list集合
     */
    List<ClazzFileTemplate> getAllClazzTemplate();

    /**
     * 根据id查询课程自评报告模版
     *
     * @return 返回ClazzFileTemplate对象
     */
    List<ClazzFileTemplate> getClazzTemplate();

    /**
     * 根据上传者id和文件对应的课程名判断是否为第一次上传
     *
     * @param userId       上传者（课程负责人）id
     * @param evaluationId 课程评审Id
     * @param fileName     文件名字
     * @return 返回一个int类型数据
     */
    ClazzFile exitFile(Integer userId, Long evaluationId, String fileName);

    /**
     * 根据上传者id和文件对应的课程名获取上传记录
     *
     * @param userId    上传者（课程负责人）id
     * @param clazzName 课程名
     * @return 返回一个ClazzFile对象
     */
    ClazzFile getClazzFile(Integer userId, String clazzName);

    /**
     * 新增课程负责人上传文件记录
     *
     * @param clazzFile 上传课程自评报告记录
     * @return 返回一个int类型数据
     */
    int saveClazzFile(ClazzFile clazzFile);

    /**
     * 根据专业获取自评报告模版
     *
     * @param major 专业
     * @return 返回根据专业查询得到的上传的课程报告的信息
     */
    ClazzFileTemplate getClazzFileTemplateByMajor(String major);

    /**
     * 修改自评报告记录
     *
     * @param loginUser 登录用户
     * @param clazzFile 上传记录
     * @return 返回一个
     */
    int updateClazzFile(User loginUser, ClazzFile clazzFile);

    /**
     * 根据专业获取自评报告
     *
     * @param major
     * @return
     */
    ClazzFileTemplate getClazzByMajor(String major);

    /**
     * 根据专业名称获取专业信息
     *
     * @param name 专业名
     * @return 返回一个Major对象
     */
    Major getMajorByName(String name);

    /**
     * 根据课程名称获得课程信息
     *
     * @param name 课程名
     * @return 返回一个Clazz对象
     */
    Clazz getClazzByName(String name);


    /**
     * 通过课程评审专家id获取这个专家所有要评审的课程信息
     *
     * @param userId
     * @return
     */
    List<GetEvaluationClazzByUserIdResult> getEvaluationClazzByUserId(Integer userId);

    /**
     * 获取所有课程评价的相关信息：文件path，评审状态，课程信息等
     *
     * @return
     */
    List<GetAllClazzInfoResult> getAllClazzInformation();





    /**
     * 课程负责人删除自己上传的文件
     *
     * @param path
     * @return
     */
    Integer deleteUploadedFile(String path);

    /**
     * 课程评审专家导出评审记录
     *
     * @param userId 课程负责人的用户id
     * @return Xlsx表格，一个文件对应一个用户，一张表格对应一场评审
     */
    XSSFWorkbook exportRecord(Integer userId);


    /**
     * 根据clazz组件获取id
     *
     * @param clazzId 课程id
     * @return 课程信息
     */
    Clazz getClazzById(Integer clazzId);


    List<ClazzVO> getClazzByUserType(User user, Integer pageNum, Integer pageSize);
}
