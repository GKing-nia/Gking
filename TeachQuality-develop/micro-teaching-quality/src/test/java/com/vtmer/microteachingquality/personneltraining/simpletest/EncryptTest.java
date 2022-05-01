package com.vtmer.microteachingquality.personneltraining.simpletest;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.junit.jupiter.api.Test;

public class EncryptTest {

    @Test
    public void test() {
        String fileName = StrUtil.uuid() + "软件工程.自评报告.docx";
        fileName = StrUtil.subBefore(fileName, StrUtil.DOT, true);
        // 随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        // 构建
        AES aes = SecureUtil.aes(key);
        // 加密为16进制表示
        String encryptHex = aes.encryptHex(fileName);
        // 解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println(encryptHex);
        System.out.println(encryptHex.length());
        System.out.println(decryptStr);
    }

    @Test
    public void test2() {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String str = key.toString();
        // System.out.println(str);
        System.out.println(IdUtil.simpleUUID());
    }

}
