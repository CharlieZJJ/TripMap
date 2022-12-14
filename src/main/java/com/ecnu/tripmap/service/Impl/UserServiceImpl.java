package com.ecnu.tripmap.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ecnu.tripmap.model.vo.PlaceVo;
import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.UserBrief;
import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.mysql.entity.Post;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.mysql.mapper.PostMapper;
import com.ecnu.tripmap.mysql.mapper.UserMapper;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.neo4j.dao.PostRepository;
import com.ecnu.tripmap.neo4j.dao.UserRepository;
import com.ecnu.tripmap.neo4j.node.PlaceNode;
import com.ecnu.tripmap.neo4j.node.PostNode;
import com.ecnu.tripmap.neo4j.node.UserNode;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.result.ResponseStatus;
import com.ecnu.tripmap.service.UserService;
import com.ecnu.tripmap.utils.CopyUtil;
import com.ecnu.tripmap.utils.RedisUtil;
import com.ecnu.tripmap.utils.SimilarityUtil;
import com.google.common.primitives.Ints;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String DEFAULT_AVATAR_PATH = "avatar_path";

    @Resource
    private UserRepository userRepository;

    @Resource
    private PlaceRepository placeRepository;

    @Resource
    private PostRepository postRepository;

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public Response login(User user) {
        String account = user.getUserAccount();
        String password = user.getUserPassword();
        User userFound = new LambdaQueryChainWrapper<>(userMapper).eq(User::getUserAccount, account)
                .one();
        if (userFound == null) {
            return Response.status(ResponseStatus.ACCOUNT_OR_PASSWORD_NOT_CORRECT);
        }
        if (passwordEncoder.matches(password, userFound.getUserPassword())) {
            return Response.success(userFound);
        }
        return Response.status(ResponseStatus.ACCOUNT_OR_PASSWORD_NOT_CORRECT);
    }

    @Override
    public Response register(User user) {
        String account = user.getUserAccount();
        List<User> list = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserAccount, account)
                .list();
        if (!list.isEmpty()) {
            return Response.status(ResponseStatus.ACCOUNT_ALREADY_EXIST);
        }

        String nickname = user.getUserNickname();
        if (nickname == null || nickname.equals("")) {
            user.setUserNickname(RandomUtil.randomString(8));
        }
        user.setUserAvatar(DEFAULT_AVATAR_PATH);
        String userPassword = user.getUserPassword();
        userPassword = passwordEncoder.encode(userPassword);
        user.setUserPassword(userPassword);
        int insert = userMapper.insert(user);
        if (insert != 1) {
            return Response.status(ResponseStatus.REGISTER_FAIL);
        }

        UserNode userNode = CopyUtil.copy(user, UserNode.class);
        userRepository.save(userNode);
        UserVo userVo = CopyUtil.copy(user, UserVo.class);
        return Response.success(userVo);
    }

    @Override
    public UserVo findUserInfo(Integer id) {
        User user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, id)
                .one();
        if (user == null)
            return null;
        return CopyUtil.copy(user, UserVo.class);
    }

    @Override
    public List<PlaceVo> findUserStoredPlace(Integer user_id) {
        List<PlaceNode> userStoredPlace = placeRepository.findUserStoredPlace(user_id);
        return CopyUtil.copyList(userStoredPlace, PlaceVo.class);
    }

    @Override
    public List<PostBrief> findCollectPostList(Integer user_id) {
        List<PostNode> userCollectedPost = postRepository.findUserCollectedPost(user_id);
        List<PostBrief> posts = new ArrayList<>();
        for (PostNode postNode : userCollectedPost) {
            Post one = new LambdaQueryChainWrapper<>(postMapper)
                    .eq(Post::getPostId, postNode.getPostId())
                    .one();
            PostBrief copy = CopyUtil.copy(one, PostBrief.class);
            String postImageList = copy.getPostImageList();
            int i = postImageList.indexOf(',');
            if (i != -1) {
                postImageList = postImageList.substring(0, i);
            }
            copy.setPostImageList(postImageList);
            String postDesc = copy.getPostDesc();
            if (postDesc.length() > 50) {
                postDesc = postDesc.substring(0, 50);
            }
            copy.setPostDesc(postDesc);
            UserNode publisher = userRepository.findPublisher(copy.getPostId());
            copy.setUserAvatar(publisher.getUserAvatar());
            copy.setUserName(publisher.getUserNickname());
            copy.setUserId(publisher.getUserId());
            posts.add(copy);
        }
        return posts;
    }

    @Override
    public List<PostBrief> findPublishPostList(Integer user_id){
        List<PostNode> userPublishPost = postRepository.findPublishPostList(user_id);
        List<PostBrief> posts = new ArrayList<>();
        for (PostNode postNode : userPublishPost) {
            Post one = new LambdaQueryChainWrapper<>(postMapper)
                    .eq(Post::getPostId, postNode.getPostId())
                    .one();
            PostBrief copy = CopyUtil.copy(one, PostBrief.class);
            String postImageList = copy.getPostImageList();
            int i = postImageList.indexOf(',');
            if (i != -1) {
                postImageList = postImageList.substring(0, i);
            }
            copy.setPostImageList(postImageList);
            String postDesc = copy.getPostDesc();
            if (postDesc.length() > 50) {
                postDesc = postDesc.substring(0, 50);
            }
            copy.setPostDesc(postDesc);
            User user = new LambdaQueryChainWrapper<>(userMapper)
                    .eq(User::getUserId,user_id)
                    .one();
            copy.setUserAvatar(user.getUserAvatar());
            copy.setUserName(user.getUserNickname());
            copy.setUserId(user.getUserId());
            posts.add(copy);
        }
        return posts;
    }

    @Override
    public List<UserBrief> findUserFollowedUser(Integer user_id) {
        List<UserNode> userFollowedUser = userRepository.findUserFollowedUser(user_id);
        List<UserBrief> users = new ArrayList<>();
        for (UserNode userNode : userFollowedUser){
            User one = new LambdaQueryChainWrapper<>(userMapper)
                    .eq(User::getUserId, userNode.getUserId())
                    .one();
            UserBrief copy = CopyUtil.copy(one,UserBrief.class);
            copy.setUserAccount(one.getUserAccount());
            copy.setUserNickname(one.getUserNickname());
            copy.setUserAvatar(one.getUserAvatar());
            copy.setUserId(one.getUserId());

            users.add(copy);
        }
        return users;
    }

    @Override
    public List<UserBrief> findUserFanUser(Integer user_id) {
        List<UserNode> userFollowedUser = userRepository.findUserFanUser(user_id);
        List<UserBrief> users = new ArrayList<>();
        for (UserNode userNode : userFollowedUser){
            User one = new LambdaQueryChainWrapper<>(userMapper)
                    .eq(User::getUserId, userNode.getUserId())
                    .one();
            UserBrief copy = CopyUtil.copy(one,UserBrief.class);
            copy.setUserAccount(one.getUserAccount());
            copy.setUserNickname(one.getUserNickname());
            copy.setUserAvatar(one.getUserAvatar());
            copy.setUserId(one.getUserId());
            users.add(copy);
        }
        return users;
    }

    @Override
    public Response followAUser(Integer user_id, Integer follow_id) {
        Integer relationship = userRepository.createFollowRelationship(user_id, follow_id);
        if (relationship == null)
            return Response.status(ResponseStatus.USER_NOT_EXIST);
        User one = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        one.setUserFollowCount(one.getUserFollowCount() + 1);
        userMapper.updateById(one);
        User one1 = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, follow_id)
                .one();
        one1.setUserFanCount(one.getUserFanCount() + 1);
        userMapper.updateById(one1);
        return Response.success(relationship);
    }

    @Override
    public Response cancelFollowAUser(Integer user_id,Integer follow_id){
        //取消关注
        userRepository.cancelFollowRelationship(user_id,follow_id);
        //关注用户
        User one = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        one.setUserFollowCount(one.getUserFollowCount() - 1);
        userMapper.updateById(one);
        //被关注用户
        User one1 = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, follow_id)
                .one();
        one1.setUserFanCount(one.getUserFanCount() - 1);
        userMapper.updateById(one1);
        asyncRecommend(user_id);
        return Response.success();
    }

    @Override
    public Response deleteAPost(Integer user_id, Integer post_id) {
        userRepository.deleteAPost(user_id,post_id);
        User one = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        one.setUserPostCount(one.getUserPostCount()-1);
        userMapper.updateById(one);
        asyncRecommend(user_id);
        return Response.success();
    }

    @Override
    public void asyncRecommend(int user_id) {
        recommend(user_id);
    }

    @Override
    public void recommend(int user_id){
        List<Integer> ret = new ArrayList<>();
        // 找到用户关注的用户列表
        List<UserBrief> users = findUserFollowedUser(user_id);
        // 如果用户尚未关注任何其他人
        if (!users.isEmpty()){
            int[] ints = new int[42];
            for (int i = 0; i < 42; i++){
                ints[i] = i;
            }
            for (int i = 0; i < 42; i++){
                int index = (int)(Math.random() * 42);
                int temp = ints[1];
                ints[1] = ints[index];
                ints[index] = temp;
            }
            ret = Ints.asList(ints);
        }
        // 如果用户关注了其他人
        else {
            HashMap<UserWithId, double[]> users_and_places = new HashMap<>();
            int user_id_cnt = 0;
            // 构建矩阵
            // 我们把用户收藏的地点看做3分
            // 把用户喜欢或收藏的文章推荐的地点看作2分
            // 把用户发表的文章推荐的地点看作1分（这种情况下用户应该已经去过这个地方，相对的兴趣可能会小一点）
            for (UserBrief userBrief : users) {
                Integer userId = userBrief.getUserId();
                double[] places = new double[42];

                // suggest
                List<PostBrief> postList = findPublishPostList(userId);
                for (PostBrief post : postList) {
                    PlaceNode recommendPlace = placeRepository.findRecommendPlace(post.getPostId());
                    places[recommendPlace.getPlaceId()] += 1;
                }

                // like
                List<PostNode> userLikedPost = postRepository.findUserLikedPost(userId);
                for (PostNode post : userLikedPost) {
                    PlaceNode recommendPlace = placeRepository.findRecommendPlace(post.getPostId());
                    places[recommendPlace.getPlaceId()] += 2;
                }

                // collects
                List<PostNode> userCollectedPost = postRepository.findUserCollectedPost(userId);
                for (PostNode post : userCollectedPost) {
                    PlaceNode recommendPlace = placeRepository.findRecommendPlace(post.getPostId());
                    places[recommendPlace.getPlaceId()] += 2;
                }

                // stores
                List<PlaceNode> userStoredPlace = placeRepository.findUserStoredPlace(userId);
                for (PlaceNode place : userStoredPlace) {
                    places[place.getPlaceId()] += 3;
                }
                users_and_places.put(new UserWithId(userId, user_id_cnt++), places);
            }
            double[] cur_user = new double[42];
            for (int i = 0; i < 42; i++){
                if (placeRepository.userSuggestSpecificPlace(user_id, i) > 0)
                    cur_user[i] += 1;
                if (placeRepository.userStoredSpecificPlace(user_id, i) > 0)
                    cur_user[i] += 3;
                if (placeRepository.userLikedSpecificPlace(user_id, i) > 0)
                    cur_user[i] += 2;
                if (placeRepository.userCollectedSpecificPlace(user_id, i) > 0)
                    cur_user[i] += 2;
            }
            users_and_places.put(new UserWithId(user_id, user_id_cnt++), cur_user);
            double[][] transform = transform(users_and_places, user_id_cnt);
            double[][] similarity = SimilarityUtil.similarity(transform, user_id_cnt);
            ret = SimilarityUtil.user_base_recommend(transform, similarity, user_id_cnt-1, user_id_cnt);
        }
        redisUtil.lSet("user_" + user_id, ret);
    }

    private double[][] transform(HashMap<UserWithId, double[]> users_with_places, int size){
        double[][] ret = new double[size][];
        for (Map.Entry<UserWithId, double[]> entry : users_with_places.entrySet()){
            ret[entry.getKey().getId()] = entry.getValue();
        }
        return ret;
    }


    @Data
    @AllArgsConstructor
    static class UserWithId{
        int user_id;
        int id;
    }

    @Data
    @AllArgsConstructor
    static class PlaceWithId{
        int place_id;
        int id;
    }

}
