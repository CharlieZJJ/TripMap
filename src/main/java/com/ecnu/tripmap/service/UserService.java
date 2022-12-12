package com.ecnu.tripmap.service;

import com.ecnu.tripmap.model.vo.PlaceVo;
import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.UserBrief;
import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.result.Response;
import io.swagger.models.auth.In;

import java.util.List;

public interface UserService {

    Response login(User user);

    Response register(User user);


    UserVo findUserInfo(Integer id);

    List<PlaceVo> findUserStoredPlace(Integer user_id);

    List<PostBrief> findCollectPostList(Integer user_id);

    List<PostBrief> findPublishPostList(Integer user_id);

    Response followAUser(Integer user_id, Integer follow_id);

    Response cancelFollowAUser(Integer user_id,Integer follow_id);

    Response deleteAPost(Integer user_id,Integer post_id);

    List<UserBrief> findUserFollowedUser(Integer user_id);

    List<UserBrief> findUserFanUser(Integer user_id);
}
