package com.vtmer.microteachingquality.model.dto.insert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Hung
 * @date 2021/11/3 0:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;

    private String userName;

    private String userPwd;

    private String realName;

    private String userType;

    private String userBelong;

    private Integer isClazz;

    private String email;
}
