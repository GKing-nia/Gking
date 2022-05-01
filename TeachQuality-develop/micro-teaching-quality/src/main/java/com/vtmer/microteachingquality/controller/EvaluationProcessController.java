package com.vtmer.microteachingquality.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.common.constant.enums.EvaluationProcessStatus;
import com.vtmer.microteachingquality.model.dto.insert.ClazzEvaluationLeaderBO;
import com.vtmer.microteachingquality.model.dto.insert.SubmitEvaluationRecordBO;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.ClazzFile;
import com.vtmer.microteachingquality.model.entity.EvaluationProcess;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.*;
import com.vtmer.microteachingquality.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Hung
 * @date 2022/4/20 19:42
 */
@RestController
@Api(tags = "课程评审相关接口")
@RequestMapping("/clazzEvaluation")
@Slf4j
public class EvaluationProcessController implements EvaluationProcessStatus {

    /**
     * 随机生成存储加密文件名(课程自评报告)的密钥
     * 固定密钥，不然会出bug
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    /**
     * 构建加密
     */
    private final AES aes = SecureUtil.aes(ENCRYPT_KEY);
    @Value("${clazz.path}")
    private String clazzPath;
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private EvaluationProcessService evaluationProcessService;
    @Autowired
    private ClazzFileService clazzFileService;
    @Resource
    private CourseEvaluationExpertService courseEvaluationExpertService;
    @Resource
    private NotifyService notifyService;

    @PostMapping("/createNewProcess")
    @ApiOperation("创建评审流程")
    public ResponseMessage<?> createNewEvaluationProcess(Integer clazzId) {
        User user = UserUtil.getCurrentUser();
        EvaluationProcess process = new EvaluationProcess(user.getId(), clazzId, EVALUATION_PROCESS_ESTABLISHED);
        return process.insert() ? ResponseMessage.newSuccessInstance("流程创建成功") : ResponseMessage.newErrorInstance("流程创建失败");
    }

    @GetMapping("/getAllEvaluationProcess")
    @ApiOperation("获取这个课程的所有评审流程")
    public ResponseMessage<List<EvaluationProcess>> getAllEvaluationProcesses(Integer clazzId) {
        User user = UserUtil.getCurrentUser();
        return ResponseMessage.newSuccessInstance(evaluationProcessService.getEvaluationProcesses(user.getId(), clazzId));
    }

    @ApiOperation("第一阶段 (课程负责人)上传已填写的自评报告")
    @PostMapping("/principalUpload")
    public ResponseMessage<?> clazzTemplateUpload(@ApiParam("选择需要上传的课程自评报告文件") @RequestPart MultipartFile file, Long evaluationId, Integer clazzId) throws IOException {
        // 获取当前登陆用户(课程负责人)对象
        User loginUser = UserUtil.getCurrentUser();

        if (ObjectUtil.isNull(file)) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        }
        if (StrUtil.isBlank(file.getOriginalFilename())) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        }

        // 目录添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File path = new File(clazzPath + File.separator + uuid);
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        // 加密文件名
        //String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        String filePath = clazzPath + File.separator + uuid + File.separator + file.getOriginalFilename();

        //此时是课程负责人登录，故userBelong是课程名
        ClazzFile clazzFile = clazzService.exitFile(loginUser.getId(), evaluationId, file.getOriginalFilename());
        if (clazzFile == null) {
            //clazzFile == null说明数据库中没有还没有这个负责人上传该课程的自评报告数据

            // 存储课程报告上传信息(修改课程报告上传者、文件路径)
            ClazzFile clazzFileInsert = new ClazzFile(loginUser.getId(), file.getOriginalFilename(), clazzId, evaluationId, filePath);
            EvaluationProcess evaluationProcess = new EvaluationProcess().selectById(evaluationId);
            evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_SUBMIT_MATERIAL);

            if (clazzFileInsert.insert() && evaluationProcess.insertOrUpdate()) {
                //发送消息给评审人
                Integer id = loginUser.getId();
                Boolean aBoolean = notifyService.sendNotificationByClazzPrincipal(id);
                if (!aBoolean){
                    return ResponseMessage.newErrorInstance("发送通知失败");
                }
                FileUtil.writeBytes(file.getBytes(), filePath);
                return ResponseMessage.newSuccessInstance("上传课程自评报告成功");
            } else {
                return ResponseMessage.newErrorInstance("上传课程自评报告失败");
            }
        } else {
            //数据库中已经有记录,报错
            return ResponseMessage.newErrorInstance("上传课程自评报告失败,已存在同名文件");
        }
    }

    @ApiOperation("获取所有课程评价的相关信息：文件path，评审状态，课程信息等")
    @GetMapping("/clazzInfo")
    public @Validated
    ResponseMessage<List<GetAllClazzInfoResult>> getAllClazzInformation() {
        return ResponseMessage.newSuccessInstance(clazzService.getAllClazzInformation());
    }

    @ApiOperation("根据专家的类型获取其所属的所有课程")
    @GetMapping("/allClazz")
    public ResponseMessage<List<ClazzVO>> getAllClazzByType(Integer pageNum, Integer pageSize) {
        User currentUser = UserUtil.getCurrentUser();
        return ResponseMessage.newSuccessInstance(clazzService.getClazzByUserType(currentUser, pageNum, pageSize));
    }

    @ApiOperation("通过专家id获取该专家要评审的所有课程信息")
    @GetMapping("/getEvaluationClazz")
    public ResponseMessage<?> getEvaluationClazzByUserId() {
        return ResponseMessage.newSuccessInstance(clazzService.getEvaluationClazzByUserId(UserUtil.getCurrentUser().getId()));
    }

    @ApiOperation("获取评审流程的课程负责人获取上传的文件信息")
    @GetMapping("/getUploadedFiles")
    public ResponseMessage<List<GetUploadedFilesResult>> getEvaluationProcessUploadFiles(Long evaluationId) {
        Integer userId = UserUtil.getCurrentUser().getId();
        return ResponseMessage.newSuccessInstance(evaluationProcessService.getUploadedFiles(userId, evaluationId));
    }

    @ApiOperation("课程负责人删除自己上传的文件")
    @PostMapping("/deleteUploadedFile")
    public ResponseMessage<?> deleteUploadedFile(String path) {
        User loginUser = UserUtil.getCurrentUser();
        boolean result = false;
        //解密路径
        String filePath = clazzPath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        ClazzFile clazzFile = clazzFileService.getClazzFile(path);

        if (!clazzFile.getUserId().equals(loginUser.getId())) {
            return ResponseMessage.newErrorInstance("并不是此用户上传的文件");
        }

        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        try {
            // 输入流
            File file = new File(filePath);
            result = file.delete();
            if (file.exists()) {
                result = false;
                log.error("用户{}删除课程自评报告失败: {}", loginUser.getRealName(), file.getName());
            } else {
                //os正常写入了
                log.info("用户 {} 删除课程自评报告成功: {}", loginUser.getRealName(), fileName);
            }
            if (clazzService.deleteUploadedFile(path) <= 0) {
                return ResponseMessage.newErrorInstance("文件已经不存在");
            }
        } catch (Exception e) {
            log.error("用户{}删除课程自评报告失败: {}", loginUser.getRealName(), e.getMessage());
        }
        return ResponseMessage.newSuccessInstance(result);
    }


    @ApiOperation("课程评审专家导出自己的记录")
    @GetMapping("/exportRecord")
    public void exportRecord(HttpServletResponse response) {
        User user = UserUtil.getCurrentUser();
        XSSFWorkbook xssfWorkbook = clazzService.exportRecord(user.getId());
        try {
            // 配置文件下载及避免呢中午呢乱码
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(new SimpleDateFormat("MM月dd日 HH时mm分ss秒").format(new Date()) + ".xlsx", "UTF-8"));

            OutputStream os = response.getOutputStream();
            xssfWorkbook.write(os);
        } catch (IOException e) {
            log.info("文件输出失败");
        }
        log.info("文件输出成功");
    }

    @ApiOperation("课程评审专家退回评审")
    @PostMapping("/sendBackEvaluation")
    public ResponseMessage<String> sendBackEvaluation(Long evaluationId) {

        return evaluationProcessService.sendBackEvaluation(evaluationId) ? ResponseMessage.newSuccessInstance("退回评审成功") : ResponseMessage.newErrorInstance("退回评审失败");
    }

    @PostMapping("/stayclazztoreview")
    @ApiOperation("分页获取待评审报告信息(暂时作废)")
    public ResponseMessage<?> stayListResponseMessage(Integer pageNum, Integer pageSize) {
        // 获取当前登陆用户(课程评审专家)对象
        User loginUser = UserUtil.getCurrentUser();
        Integer userId = loginUser.getId();
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.selectCourseMessageofEvaluationExpert(userId, 0, pageNum, pageSize));
    }

    @PostMapping("/finishclazztoreview")
    @ApiOperation("分页获取 课程评审专家已评审报告信息(暂时作废)")
    public ResponseMessage<?> finishListResponseMessage(Integer pageNum, Integer pageSize) {
        // 获取当前登陆用户(课程评审专家)对象
        User loginUser = UserUtil.getCurrentUser();
        Integer userId = loginUser.getId();
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.selectCourseMessageofEvaluationExpert(userId, 1, pageNum, pageSize));
    }

    @GetMapping("/getEvaluationReviewOptions")
    @ApiOperation("获取评审信息（指标和具体内容）")
    public ResponseMessage<List<ClazzInJudgeResultDTO>> inJude(Integer clazzId) {
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.getClazzInJudgeByClazzType(clazzId));
    }

    @GetMapping("/getMyselfFinishedReviewInfo")
    @ApiOperation("课程专家 获取自己的评审信息（指标、具体内容和选项）")
    public ResponseMessage<ClazzFinishResult> getMyselfFinishReview(Long evaluationId) {
        // 获取当前登陆用户(课程评审专家)对象
        User user = UserUtil.getCurrentUser();
        return getFinishReview(evaluationId, user.getId());
    }

    @GetMapping("/getExpertFinishedReviewInfo")
    @ApiOperation("根据评审id和userId获取 这个评审的这个专家评审的内容")
    public ResponseMessage<ClazzFinishResult> getFinishReview(Long evaluationId, Integer userid) {
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.getSingleClazzFinishReview(evaluationId, userid));
    }

    @GetMapping("/evaluationFinishedReviews/{evaluationId}")
    @ApiOperation("课程专家组长获取 该评审全部已评审信息（指标、具体内容和选项）")
    public ResponseMessage<List<FinishedReviewVO>> getAllFinishReviews(@RequestParam("evaluationId") @NotNull Long evaluationId) {
        // 获取当前登陆用户(课程评审专家)对象
        return ResponseMessage.newSuccessInstance(evaluationProcessService.getAllFinishedReviews(evaluationId));
    }

    @PostMapping("/submitEvaluation")
    @ApiOperation("课程评审专家和课程评审专家组长上传答题记录")
    public ResponseMessage<String> submitEvaluationRecord(@RequestBody @NotNull SubmitEvaluationRecordBO submitEvaluationRecordBO) {
        //TODO 更改课程评审专家组长
        return courseEvaluationExpertService.insertEvaluationRecord(submitEvaluationRecordBO) ? ResponseMessage.newSuccessInstance("评审成功") : ResponseMessage.newErrorInstance("评审失败，请重新尝试");
    }

    @GetMapping("/getEvaluationFiles")
    @ApiOperation("课程评审专家获取课程文件列表")
    public ResponseMessage<List<GetClazzFilesResult>> getEvaluationFiles(Long evaluationId) {
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.getAllEvaluationFiles(evaluationId));
    }

    @PostMapping("/insertEmptyData")
    @ApiOperation("插入空白数据用接口（后台用的，前端别测试这个）")
    public ResponseMessage insertEmptyData(String tableName, Integer size) {
        return ResponseMessage.newSuccessInstance(courseEvaluationExpertService.insertEmptyData(tableName, size));
    }

    @ApiOperation("保存专家组长的小组意见")
    @PostMapping("/postLeaderReview")
    public ResponseMessage<String> postClazzEvaluationLeaderReview(ClazzEvaluationLeaderBO evaluationLeaderBO) {
        return evaluationProcessService.postClazzEvaluationLeaderReview(evaluationLeaderBO) ? ResponseMessage.newSuccessInstance("评审成功") : ResponseMessage.newErrorInstance("评审失败");
    }

    @GetMapping("/getLeaderReviews")
    @ApiOperation("查看 这个评审流程有多少个专家组长的小组意见")
    public ResponseMessage<List<ClazzEvaluationLeaderReviewVO>> getClazzEvaluationLeaderReviews(Long evaluationId) {
        return ResponseMessage.newSuccessInstance(evaluationProcessService.getClazzEvaluationLeaderReviews(evaluationId));
    }

    @PostMapping("/setEvaluationEnd")
    @ApiOperation("结束评审流程")
    public ResponseMessage<String> setEvaluationEnd(Long evaluationId) {
        EvaluationProcess evaluationProcess = new EvaluationProcess().selectById(evaluationId);
        evaluationProcess.setCurrentPhases(EVALUATION_PROCESS_END);
        return evaluationProcess.insertOrUpdate() ? ResponseMessage.newSuccessInstance("评审流程结束") : ResponseMessage.newErrorInstance("请重试结束评审流程");
    }
}
