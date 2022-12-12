package com.ecnu.tripmap.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ecnu.tripmap.mysql.entity.Topic;
import com.ecnu.tripmap.neo4j.node.PlaceNode;
import com.ecnu.tripmap.neo4j.node.TopicNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostVo {
    private Integer postId;

    private Date postPublishTime;

    private String postImageList;

    private String postDesc;

    private Integer postCollectCount;

    private Integer postLikeCount;

    private String postTitle;

    private String userNickName;

    private String userAvatar;

    private Integer userId;

    private boolean isLiked;

    private boolean isCollected;

    private boolean isFollowed;


    private String recommendPlace;
    private Integer recommendPlaceId;
    private boolean isPlaceCollected;

    private List<Topic> topicList;
}
