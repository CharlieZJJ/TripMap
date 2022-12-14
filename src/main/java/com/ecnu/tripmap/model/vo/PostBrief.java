package com.ecnu.tripmap.model.vo;

import com.ecnu.tripmap.mysql.entity.Post;
import com.ecnu.tripmap.utils.CopyUtil;
import com.qcloud.cos.transfer.Copy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostBrief {

    private Integer postId;

    private String postImageList;

    private String postDesc;

    private Integer postLikeCount;

    private String postTitle;

    private String userName;

    private String userAvatar;

    private Integer userId;
}

