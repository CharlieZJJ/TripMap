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
import io.swagger.models.auth.In;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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
    private HttpSession session;

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
        return Response.success();
    }

}
