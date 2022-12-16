package com.ecnu.tripmap.service.Impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ecnu.tripmap.model.vo.PlaceBiref;
import com.ecnu.tripmap.mysql.entity.Place;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.mysql.mapper.PlaceMapper;
import com.ecnu.tripmap.mysql.mapper.UserMapper;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.result.ResponseStatus;
import com.ecnu.tripmap.service.PlaceService;
import com.ecnu.tripmap.service.UserService;
import com.ecnu.tripmap.utils.CopyUtil;
import com.ecnu.tripmap.utils.RedisUtil;
import com.ecnu.tripmap.utils.SimilarityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class PlaceServiceImpl implements PlaceService {

    @Resource
    private PlaceRepository placeRepository;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PlaceMapper placeMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    public Response collectPlace(Integer user_id, Integer place_id){
        Integer collectRelationship = placeRepository.createStoreRelationship(user_id,place_id);
        if (collectRelationship == null)
            return Response.status(ResponseStatus.PLACE_NOT_EXIST);
        User user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        user.setUserCollectLocationCount(user.getUserCollectLocationCount() + 1);
        userMapper.updateById(user);
        userService.asyncRecommend(user_id);
        return Response.success(collectRelationship);
    }

    public Response cancelCollectPlace(Integer user_id,Integer place_id){
        //删除收藏关系
        placeRepository.cancelStoreRelationship(user_id,place_id);
        User user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        user.setUserCollectLocationCount(user.getUserCollectLocationCount() - 1);
        userMapper.updateById(user);
        userService.asyncRecommend(user_id);
        return Response.success();

    }

    public List<PlaceBiref> recommendPlaces(Integer user_id){
        if (!redisUtil.hasKey("user_"+user_id)) {
            userService.recommend(user_id);
        }
        List<Object> objects = redisUtil.lGet("user_" + user_id, 0, -1);
        List<Integer> fromList = SimilarityUtil.getFromList(objects);
        List<PlaceBiref> places = new ArrayList<>();
        for (int i = 0;i < 10; i++){
            Integer placeID = fromList.get(i);
            Place one = new LambdaQueryChainWrapper<>(placeMapper)
                    .eq(Place::getPlaceId, placeID)
                    .one();
            PlaceBiref copy = CopyUtil.copy(one,PlaceBiref.class);
            places.add(copy);
        }
        return places;
    }

}
