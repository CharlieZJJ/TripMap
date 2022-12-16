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
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

public class SimilarityUtil {
    public static double cos_sim(double[] x, double[] y){
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

    public static List<Integer> getFromList(List<Object> list){
        String[] split = list.get(0).toString().split(",");
        ArrayList<Integer> integers = new ArrayList<>();
        for (String s : split) {
            integers.add(Integer.parseInt(s));
        }
        return integers;
    }

    public static double[][] similarity(double[][] users_and_laces, int user_size){
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

    public static List<Integer> user_base_recommend(double[][] data, double[][] simi, int id, int size){
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


    public static List<Integer> sortIndex(double[] data){
        List<Integer> places = new ArrayList<>();
        for(int i = 0; i < data.length; i++) {
            int maxIndex = maxIndex(data);
            data[maxIndex] = 0;
            places.add(maxIndex);
        }
        return places;
    }
}
