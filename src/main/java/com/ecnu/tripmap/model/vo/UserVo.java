package com.ecnu.tripmap.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private Integer userId;

    private String userNickname;

    private String userAvatar;

    private String userAccount;

    private Date userCreateTime;

    private Integer userFanCount;

    private Integer userFollowCount;

    private Integer userPostCount;

    private Integer userCollectPostCount;

    private Integer userCollectLocationCount;
}
