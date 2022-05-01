package com.vtmer.microteachingquality.personneltraining;

import com.vtmer.microteachingquality.mapper.ClazzFileMapper;
import com.vtmer.microteachingquality.mapper.ClazzMapper;
import com.vtmer.microteachingquality.mapper.UserMapper;
import com.vtmer.microteachingquality.mapper.UserRoleMapper;
import com.vtmer.microteachingquality.model.entity.ClazzFile;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.model.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MyBatisGenerator {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ClazzFileMapper clazzFileMapper;
    @Autowired
    private ClazzMapper clazzMapper;

    @Test
    public void testMbg() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("src/main/resources/generationConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        org.mybatis.generator.api.MyBatisGenerator myBatisGenerator = new org.mybatis.generator.api.MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    @Test
    public void insertUserRole() {
        List<User> users = userMapper.selectAll();
        users.forEach(user -> {
            userRoleMapper.insertRecord(new UserRole(user.getId(), 1));
        });
    }

    @Test
    public void reviseUserPassword() {
        List<User> users = userMapper.selectAll();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        users.forEach(user -> userMapper.updatePwdByUserId(passwordEncoder.encode(user.getUserName()), user.getId()));
    }

    @Test
    public void setClazzIdInClazzFile() {
        ClazzFile clazzFile = new ClazzFile();
        List<ClazzFile> clazzFiles = clazzFile.selectAll();
        clazzFiles.forEach(clazzFile1 -> {
            clazzFile1.setClazzId(clazzMapper.selectByName(clazzFile1.getClazzName()).getId());
            clazzFile1.insertOrUpdate();
        });
    }

}
