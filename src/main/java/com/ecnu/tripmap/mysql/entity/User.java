package com.ecnu.tripmap.mysql.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer userId;

    @TableField("user_nickname")
    private String userNickname;

    @TableField("user_avatar")
    private String userAvatar;

    @TableField("user_password")
    private String userPassword;

    @NotBlank
    @NotEmpty
    @TableField("user_account")
    private String userAccount;

    @TableField(value = "user_create_time", fill = FieldFill.INSERT)
    private Date userCreateTime;

    @TableField("user_fan_count")
    private Integer userFanCount = 0;

    @TableField("user_follow_count")
    private Integer userFollowCount = 0;

    @TableField("user_post_count")
    private Integer userPostCount = 0;

    @TableField("user_collect_post_count")
    private Integer userCollectPostCount = 0;

    @TableField("user_collect_location_count")
    private Integer userCollectLocationCount = 0;

    public User(String userPassword, String userAccount) {
        this.userPassword = userPassword;
        this.userAccount = userAccount;
    }
}
