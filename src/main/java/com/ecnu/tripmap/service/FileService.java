package com.ecnu.tripmap.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * ClassName: com.ecnu.juzimang.service.FileService
 * Created by zjj
 * Date: 2022-02-09 11:17
 */
public interface FileService {
    /**
     * 处理浏览器文件上传请求
     */
    String upload(MultipartFile multipartFile);

    /**
     * 处理普通文件上传
     */
    String upload(File file);

    void delete(String key);
}
