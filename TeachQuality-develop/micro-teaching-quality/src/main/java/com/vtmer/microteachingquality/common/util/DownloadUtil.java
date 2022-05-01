package com.vtmer.microteachingquality.common.util;

import cn.hutool.core.util.ObjectUtil;
import com.vtmer.microteachingquality.common.exception.report.ReportTemplateNotExistException;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 文件工具类
 */
public class DownloadUtil {
    public static void downloadFile(String fileName, String filePath, HttpServletResponse response) {
        BufferedInputStream bis = null;
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 输入流
            fis = new FileInputStream(filePath);
            bis = new BufferedInputStream(fis);
            //输出流
            os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int temp = 0;
            while ((temp = bis.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            os.flush();
        } catch (IOException e) {
            throw new ReportTemplateNotExistException();
        } finally {
            if (ObjectUtil.isNotNull(bis)) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ObjectUtil.isNotNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
