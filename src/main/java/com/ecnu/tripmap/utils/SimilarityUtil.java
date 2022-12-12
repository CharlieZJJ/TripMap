package com.ecnu.tripmap.utils;

import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.model.vo.UserBrief;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.neo4j.dao.PostRepository;
import com.ecnu.tripmap.neo4j.node.PlaceNode;
import com.ecnu.tripmap.neo4j.node.PostNode;
import com.ecnu.tripmap.service.UserService;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class SimilarityUtil {

    @Resource
    private UserService userService;

    @Resource
    private PlaceRepository placeRepository;

    @Resource
    private PostRepository postRepository;

    public List<Integer> recommend(int user_id){
        List<Integer> ret = new ArrayList<>();
        // 找到用户关注的用户列表
        List<UserBrief> users = userService.findUserFollowedUser(user_id);
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
                List<PostBrief> postList = userService.findPublishPostList(userId);
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
            double[][] similarity = similarity(transform, user_id_cnt);
            ret = user_base_recommend(transform, similarity, user_id_cnt-1, user_id_cnt);
        }
        return ret;
    }

    private double[][] transform(HashMap<UserWithId, double[]> users_with_places, int size){
        double[][] ret = new double[size][];
        for (Map.Entry<UserWithId, double[]> entry : users_with_places.entrySet()){
            ret[entry.getKey().getId()] = entry.getValue();
        }
        return ret;
    }

    private double cos_sim(double[] x, double[] y){
        assert x.length == y.length;
        double numerator = 0;
        for (int i = 0; i < x.length; i++){
            numerator += x[i] * y[i];
        }
        double denominatorx = 0;
        for (double j : x) {
            denominatorx += j * j;
        }
        double denominatory = 0;
        for (double j : y) {
            denominatory += j * j;
        }
        double sqrt = Math.sqrt(denominatorx * denominatory);
        return numerator / sqrt;
    }

    private double[][] similarity(double[][] users_and_laces, int user_size){
        double[][] w = new double[user_size][user_size];
        for (int i = 0; i < user_size; i++){
            for (int j = i; j < user_size; j++){
                if (i != j){
                    w[i][j] = cos_sim(users_and_laces[i], users_and_laces[j]);
                    w[j][i] = w[i][j];
                }else{
                    w[i][j] = 0;
                }
            }
        }
        return w;
    }

    private List<Integer> user_base_recommend(double[][] data, double[][] simi, int id, int size){
        int place_size = 42;
        double[] user_data = data[id];
        HashSet<Integer> not = new HashSet<>();
        for (int i = 0; i < place_size; i++){
            if (user_data[i] == 0)
                not.add(i);
        }
        for (Integer integer : not) {
            for (int j = 0; j < size; j++){
                if (data[j][integer] != 0)
                    user_data[integer] += simi[id][j] * data[j][integer];
            }
        }
        return sortIndex(user_data);
    }

    //获取最大值索引
    public static int maxIndex(double[] arr){
        int maxIndex=0;;
        for(int i=0;i<arr.length;i++){
            if(arr[i]>arr[maxIndex]){
                maxIndex=i;
            }
        }
        return maxIndex;
    }


    private List<Integer> sortIndex(double[] data){
        List<Integer> places = new ArrayList<>();
        for(int i = 0; i < data.length; i++) {
            int maxIndex = maxIndex(data);
            data[maxIndex] = 0;
            places.add(maxIndex);
        }
        return places;
    }

//    public static void main(String[] args) {
//        double[] doubles = new double[]{2.4, 2.7, 1.3, 0.634, 0.635};
//        SimilarityUtil similarityUtil = new SimilarityUtil();
//        List<Integer> integers = similarityUtil.top_n(doubles);
//        for (Integer integer : integers) {
//            System.out.println(integer);
//        }
//    }

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
