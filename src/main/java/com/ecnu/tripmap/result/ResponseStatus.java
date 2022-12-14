package com.ecnu.tripmap.result;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {

    // -1用作exception的code， 见Aspect/GlobalExceptionHandler
    OK(0, "操作成功"),
    ACCOUNT_OR_PASSWORD_NOT_CORRECT(-2, "账号或密码不正确"),
    ACCOUNT_ALREADY_EXIST(-3, "账号已经存在"),
    REGISTER_FAIL(-4, "注册失败，请联系工作人员"),
    USER_NOT_EXIST(-5, "用户不存在"),
    NOT_LOGIN(-6, "尚未登录"),
    POST_NOT_EXIST(-7, "笔记不存在"),
    PLACE_NOT_EXIST(-8,"地点不存在"),
    PUBLISH_FAIL(-9,"笔记发布失败，请联系工作人员"),
    HOME_PAGE_FAIL(-10,"首页笔记获取失败，请联系工作人员"),
    PLACE_RECOMMEND_FAIL(-11,"推荐地点获取失败，请联系工作人员"),
    PLACE_POSTS_FAIL(-12,""),
    PICTURE_UPLOAD_FAIL(-13, "图片上传失败");



    private final Integer code;

    private final String message;

}
