package com.ecnu.tripmap.service;

import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.PostPv;
import com.ecnu.tripmap.model.vo.PostVo;
import com.ecnu.tripmap.result.Response;

import java.util.List;

public interface PostService {

    Response collectPost(Integer user_id, Integer post_id);

    Response cancelCollectPost(Integer user_id,Integer post_id);

    Response likePost(Integer user_id, Integer post_id);

    Response cancelLikePost(Integer user_id,Integer post_id);

    PostVo findPostInfo(Integer post_id, Integer user_id);

    PostVo publish(PostPv postPv, Integer user_id);
    List<PostBrief> postList(Integer user_id);
    List<PostBrief> placePostList(Integer place_id);
}
