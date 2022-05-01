package com.vtmer.microteachingquality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vtmer.microteachingquality.mapper.ClazzFileMapper;
import com.vtmer.microteachingquality.model.entity.ClazzFile;
import com.vtmer.microteachingquality.service.ClazzFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hung
 * @date 2022/4/22 0:23
 */
@Service
public class ClazzFileServiceImpl extends ServiceImpl<ClazzFileMapper, ClazzFile> implements ClazzFileService {

    @Autowired
    private ClazzFileMapper clazzFileMapper;

    @Override
    public ClazzFile getClazzFile(String path) {
        QueryWrapper<ClazzFile> clazzFileQueryWrapper = new QueryWrapper<>();
        clazzFileQueryWrapper.eq("path", path);
        return clazzFileMapper.selectOne(clazzFileQueryWrapper);
    }
}
