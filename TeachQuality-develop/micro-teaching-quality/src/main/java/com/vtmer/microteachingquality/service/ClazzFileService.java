package com.vtmer.microteachingquality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vtmer.microteachingquality.model.entity.ClazzFile;

/**
 * @author Hung
 * @date 2022/4/22 0:22
 */
public interface ClazzFileService extends IService<ClazzFile> {
    ClazzFile getClazzFile(String path);


}
