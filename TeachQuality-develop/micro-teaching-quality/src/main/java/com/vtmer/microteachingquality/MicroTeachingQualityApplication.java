package com.vtmer.microteachingquality;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Hung
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.vtmer.microteachingquality.mapper")
public class MicroTeachingQualityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroTeachingQualityApplication.class, args);
    }

}
