package com.ecnu.tripmap.service.Impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ecnu.tripmap.model.vo.PlaceVo;
import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.UserBrief;
import com.ecnu.tripmap.model.vo.UserSearchResult;
import com.ecnu.tripmap.mysql.entity.Post;
import com.ecnu.tripmap.mysql.mapper.PostMapper;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.neo4j.dao.UserRepository;
import com.ecnu.tripmap.neo4j.node.PlaceNode;
import com.ecnu.tripmap.neo4j.node.UserNode;
import com.ecnu.tripmap.service.PostService;
import com.ecnu.tripmap.service.SearchService;
import com.ecnu.tripmap.utils.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private PlaceRepository placeRepository;

    @Resource
    private PostMapper postMapper;
    @Resource
    private PostService postService;

    @Override
    public HashMap<String, Object> search(String query, int type, int user_id) {
        HashMap<String, Object> ret = new HashMap<>();
        switch (type){
            case 1:
                List<UserNode> userNodes = userRepository.fuzzySearch(query);
                List<UserSearchResult> userSearchResults = new ArrayList<>();
                for (UserNode userNode : userNodes) {
                    UserSearchResult copy = CopyUtil.copy(userNode, UserSearchResult.class);
                    Integer followed = userRepository.isFollowed(user_id, userNode.getUserId());
                    if (followed != null)
                        copy.setFollowed(true);
                    userSearchResults.add(copy);
                }
                ret.put("user", userSearchResults);
                break;
            case 2:
                List<PlaceNode> placeNodes = placeRepository.fuzzySearch(query);
                List<PlaceVo> placeVos = CopyUtil.copyList(placeNodes, PlaceVo.class);
                ret.put("place", placeVos);
                break;
            default:
                List<Post> list = new LambdaQueryChainWrapper<>(postMapper).like(Post::getPostDesc, query)
                        .or().like(Post::getPostTitle, query)
                        .list();
                ArrayList<PostBrief> postBriefs = new ArrayList<>();
                for (Post post : list) {
                    postBriefs.add(postService.getFromPost(post));
                }
                ret.put("post", postBriefs);
                break;
        }
        return ret;
    }
}
