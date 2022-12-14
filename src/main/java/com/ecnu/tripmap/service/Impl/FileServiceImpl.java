package com.ecnu.tripmap.service.Impl;

import com.ecnu.tripmap.service.FileService;
import com.ecnu.tripmap.utils.QCloudCosUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    private QCloudCosUtils qCloudCosUtils;

    @Override
    public String upload(MultipartFile multipartFile) {
        return qCloudCosUtils.upload(multipartFile);
    }

    @Override
    public String upload(File file) {
        return qCloudCosUtils.upload(file);
    }

    @Override
    public void delete(String key) {
        qCloudCosUtils.delete(key);
    }
}
