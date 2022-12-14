package com.ecnu.tripmap.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 腾讯云对象存储工具类
 */
@Data
public class QCloudCosUtils {
    //API密钥secretId
    private String secretId;
    //API密钥secretKey
    private String secretKey;
    //存储桶所属地域
    private String region;
    //存储桶空间名称
    private String bucketName;
    //存储桶访问域名
    private String url;
    //上传文件前缀路径(eg:/images/)
    private String prefix;

    private String appId;

    /**
     * 上传File类型的文件
     */
    public String upload(File file) {
        //生成唯一文件名
        String newFileName = generateUniqueName(file.getName());
        //文件在存储桶中的key
        String key = prefix + newFileName;
        //声明客户端
        COSClient cosClient = null;
        try {
            COSCredentials cosCredentials = new BasicCOSCredentials(secretId, secretKey);
            //设置bucket的区域
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(cosCredentials, clientConfig);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            return url + key;
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return null;
    }

    /**
     * upload()重载方法
     */
    public String upload(MultipartFile multipartFile) {
        //生成唯一文件名
        String newFileName = generateUniqueName(multipartFile.getOriginalFilename());
        //文件在存储桶中的key
        String key = prefix + newFileName;
        //准备将MultipartFile类型转为File类型
        COSClient cosClient = null;
        File file = null;
        try {
            COSCredentials cosCredentials = new BasicCOSCredentials(secretId, secretKey);
            //设置bucket的区域
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(cosCredentials, clientConfig);
            //生成临时文件
            file = File.createTempFile("temp", null);
            //将MultipartFile类型转为File类型
            multipartFile.transferTo(file);
            //创建存储对象的请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            //执行上传并返回结果信息
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            return url + key;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
        return null;
    }

    /**
     * 根据UUID生成唯一文件名
     */
    public String generateUniqueName(String originalName) {
        return UUID.randomUUID() + originalName.substring(originalName.lastIndexOf("."));
    }

    public void delete(String key) {
        COSClient cosClient = null;
        key = getPrefix() + key;
        try {
            COSCredentials cosCredentials = new BasicCOSCredentials(secretId, secretKey);
            //设置bucket的区域
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(cosCredentials, clientConfig);
            cosClient.deleteObject(getBucketName(), key);
        } catch (CosServiceException e) { // 如果是其他错误, 比如参数错误， 身份验证不过等会抛出CosServiceException
            e.printStackTrace();
        }finally {
            // 关闭客户端
            cosClient.shutdown();
        }

    }

}