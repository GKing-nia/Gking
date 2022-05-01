package com.vtmer.microteachingquality.model.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Hung
 * @date 2022/4/23 22:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinishedReviewVO {
    private Integer userId;
    private String realName;
    private LocalDateTime reviewTime;
}
