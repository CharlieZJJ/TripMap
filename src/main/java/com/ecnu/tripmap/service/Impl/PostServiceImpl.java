package com.ecnu.tripmap.service.Impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.PostPv;
import com.ecnu.tripmap.model.vo.PostVo;
import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.mysql.entity.Post;
import com.ecnu.tripmap.mysql.entity.Topic;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.mysql.mapper.PostMapper;
import com.ecnu.tripmap.mysql.mapper.TopicMapper;
import com.ecnu.tripmap.mysql.mapper.UserMapper;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.neo4j.dao.PostRepository;
import com.ecnu.tripmap.neo4j.dao.TopicRepository;
import com.ecnu.tripmap.neo4j.dao.UserRepository;
import com.ecnu.tripmap.neo4j.node.PlaceNode;
import com.ecnu.tripmap.neo4j.node.PostNode;
import com.ecnu.tripmap.neo4j.node.TopicNode;
import com.ecnu.tripmap.neo4j.node.UserNode;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.result.ResponseStatus;
import com.ecnu.tripmap.service.PostService;
import com.ecnu.tripmap.utils.CopyUtil;
import com.ecnu.tripmap.utils.SimilarityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostRepository postRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private PlaceRepository placeRepository;

    @Resource
    private TopicRepository topicRepository;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private SimilarityUtil similarityUtil;

    @Override
    public Response collectPost(Integer user_id, Integer post_id) {
        Integer collectRelationship = postRepository.createCollectRelationship(user_id, post_id);
        if (collectRelationship == null)
            return Response.status(ResponseStatus.POST_NOT_EXIST);
        Post post = new LambdaQueryChainWrapper<>(postMapper)
                .eq(Post::getPostId, post_id)
                .one();
        post.setPostCollectCount(post.getPostCollectCount() + 1);
        postMapper.updateById(post);
        User user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        user.setUserCollectPostCount(user.getUserCollectPostCount() + 1);
        userMapper.updateById(user);
        return Response.success(collectRelationship);
    }
    public  Response cancelCollectPost(Integer user_id,Integer post_id){
        //取消收藏
        postRepository.cancelPostCollect(user_id,post_id);
        //更新
        Post post = new LambdaQueryChainWrapper<>(postMapper)
                .eq(Post::getPostId, post_id)
                .one();
        post.setPostCollectCount(post.getPostCollectCount() - 1);
        postMapper.updateById(post);
        User user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getUserId, user_id)
                .one();
        user.setUserCollectPostCount(user.getUserCollectPostCount() - 1);
        userMapper.updateById(user);
        return Response.success();
    }

    @Override
    public Response likePost(Integer user_id, Integer post_id) {
        Integer likeRelationship = postRepository.createLikeRelationship(user_id,post_id);
        if (likeRelationship == null)
            return Response.status(ResponseStatus.POST_NOT_EXIST);
        Post post = new LambdaQueryChainWrapper<>(postMapper)
                .eq(Post::getPostId, post_id)
                .one();
        post.setPostLikeCount(post.getPostLikeCount() + 1);
        postMapper.updateById(post);
        return Response.success(likeRelationship);
    }

    @Override
    public Response cancelLikePost(Integer user_id, Integer post_id) {
        postRepository.cancelLikePost(user_id,post_id);
        Post post = new LambdaQueryChainWrapper<>(postMapper)
                .eq(Post::getPostId, post_id)
                .one();
        post.setPostLikeCount(post.getPostLikeCount() - 1);
        postMapper.updateById(post);
        return Response.success();
    }

    @Override
    public PostVo findPostInfo(Integer post_id, Integer user_id){
        Post post = new LambdaQueryChainWrapper<>(postMapper)
                .eq(Post::getPostId,post_id)
                .one();
        if (post == null)
            return null;
        PostVo copy = CopyUtil.copy(post,PostVo.class);

//        用户相关信息
        UserNode publisher = userRepository.findPublisher(post_id);
        copy.setUserId(publisher.getUserId());
        copy.setUserAvatar(publisher.getUserAvatar());
        copy.setUserNickName(publisher.getUserNickname());


        PlaceNode place = placeRepository.findRecommendPlace(post_id);

        copy.setRecommendPlaceId(place.getPlaceId());
        copy.setRecommendPlace(place.getPlaceAddress());
        //是否关注，点赞，收藏
        Integer isLiked = postRepository.isLiked(user_id, post_id);
        if (isLiked == null)
            copy.setLiked(false);
        else copy.setLiked(true);

        Integer isCollected = postRepository.isCollected(user_id,post_id);
        if (isCollected == null)
            copy.setCollected(false);
        else copy.setCollected(true);

        Integer isFollowed = userRepository.isFollowed(user_id,publisher.getUserId());
        if (isFollowed == null)
            copy.setFollowed(false);
        else copy.setFollowed(true);
        //是否收藏地点
        Integer isPlaceCollected=postRepository.isPlaceCollected(user_id, place.getPlaceId());
        if(isPlaceCollected==null)
            copy.setPlaceCollected(false);
        else copy.setPlaceCollected(true);

        //话题列表
        List<TopicNode> topics = topicRepository.findPostTopics(post_id);
        List<Topic> topicList = new ArrayList<>();
        Topic topic = new Topic();
        TopicNode topicNode = new TopicNode();
        for (int i = 0; i < topics.size(); i++){
            topicNode = topics.get(i);
            topic.setTopicId(topicNode.getTopicId());
            topic.setTopicName(topicNode.getTopicName());
            topicList.add(topic);
        }
        copy.setTopicList(topicList);
        return copy;
    }

    @Override
    public PostVo publish(PostPv postPv, Integer user_id){
        Post post = CopyUtil.copy(postPv,Post.class);
        post.setPostPublishTime(new Date());
        post.setPostCollectCount(0);
        post.setPostLikeCount(0);
        int insert = postMapper.insert(post);
        if (insert != 1){
            return null;
        }


//        PostNode postNode = CopyUtil.copy(postPv,PostNode.class);
//        postRepository.save(postNode);
        PostNode postNode = postRepository.createPostNode(post.getPostId());
        Integer publishRelationShip = postRepository.createPublishRelationship(post.getPostId(),user_id);
        if (publishRelationShip == null)
            return null;

        List<String> topicNames = postPv.getTopicList();
        List<Topic> topics = new ArrayList<>();
        if (topicNames != null){
            for (int i = 0; i < topicNames.size();i++){
                String topicname = topicNames.get(i);
                List<Topic> topiclist = new LambdaQueryChainWrapper<>(topicMapper)
                        .eq(Topic::getTopicName, topicname)
                        .list();
                Topic topic = new Topic();
                if (topiclist == null){
                    topic.setTopicName(topicname);
                    int insertTopic = topicMapper.insert(topic);
                    if (insertTopic != 1)
                        return null;
                    TopicNode topicNode = topicRepository.createTopicNode(topic.getTopicId(),topicname);
                    if (topicNode == null)
                        return null;
                }else {
                    topic = topiclist.get(0);
                }
                Integer belong = topicRepository.createBelongRelationship(post.getPostId(),topic.getTopicId());
                if (belong == null)
                    return null;
                topics.add(topic);
            }
        }


        PlaceNode placeNode = placeRepository.findPlaceByAddress(postPv.getRecommendPlace());
        Integer recommend = placeRepository.createRecommendRelationship(post.getPostId(),placeNode.getPlaceId());
        if (recommend == null)
            return null;

        UserNode user = userRepository.fineUserById(user_id);

        PostVo postVo = CopyUtil.copy(post,PostVo.class);
        postVo.setUserId(user.getUserId());
        postVo.setUserNickName(user.getUserNickname());
        postVo.setUserAvatar(user.getUserAvatar());
        postVo.setLiked(false);
        postVo.setCollected(false);
        Integer isFollowed = userRepository.isFollowed(user.getUserId(),user.getUserId());
        if (isFollowed == null)
            postVo.setFollowed(false);
        else postVo.setFollowed(true);

        postVo.setRecommendPlace(placeNode.getPlaceAddress());
        postVo.setRecommendPlaceId(placeNode.getPlaceId());
        postVo.setTopicList(topics);

        return postVo;

    }

    @Override
    public List<PostBrief> postList(Integer user_id){

        List<Integer> placesId = similarityUtil.recommend(user_id);
        List<PostNode> postNodes = new ArrayList<>();
        List<PostBrief> posts = new ArrayList<>();
        for (int i = 0;i < 10; i++){
            Integer placeID = placesId.get(i);
            List<PostNode> places_post = postRepository.findPlacePostList(placeID);
            postNodes.addAll(places_post);
        }
        for (PostNode post : postNodes){
            Post one = new LambdaQueryChainWrapper<>(postMapper)
                    .eq(Post::getPostId, post.getPostId())
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
        Collections.shuffle(posts);
        return posts;
    }


    @Override
    public List<PostBrief> placePostList(Integer place_id) {
        List<PostNode> postNodes = postRepository.findPlacePostList(place_id);
        List<PostBrief> posts = new ArrayList<>();
        for (PostNode post : postNodes){
            Post one = new LambdaQueryChainWrapper<>(postMapper)
                    .eq(Post::getPostId, post.getPostId())
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
}
