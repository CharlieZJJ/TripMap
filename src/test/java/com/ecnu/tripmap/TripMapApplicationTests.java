package com.ecnu.tripmap;

import com.ecnu.tripmap.model.vo.PlaceBiref;
import com.ecnu.tripmap.model.vo.PostBrief;
import com.ecnu.tripmap.mysql.entity.Post;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.neo4j.dao.PostRepository;
import com.ecnu.tripmap.neo4j.dao.UserRepository;
import com.ecnu.tripmap.service.Impl.UserServiceImpl;
import com.ecnu.tripmap.service.PlaceService;
import com.ecnu.tripmap.service.SearchService;
import com.ecnu.tripmap.service.UserService;
import com.ecnu.tripmap.utils.CopyUtil;
import com.ecnu.tripmap.utils.SimilarityUtil;
import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class TripMapApplicationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SearchService searchService;

    @Test
    void testSave() {
//int i = 0;
//int greater = 0;
//int equal = 0;
//int le = 0;
//        for (; i < 1000; i++) {
//            UserVo userInfo = userService.findUserInfo(i + 1);
//            List<PlaceNode> userStoredPlace = placeRepository.findUserStoredPlace(i + 1);
//            if (userInfo.getUserCollectLocationCount() > userStoredPlace.size()) {
//               le++;
//            }else if(userInfo.getUserCollectLocationCount() == userStoredPlace.size()){
//                equal++;
//            }else greater++;
//
//        }
//        System.out.println(le);
//        System.out.println(equal);
//        System.out.println(greater);
        Integer followRelationship = userRepository.createFollowRelationship(1, 1111);
        System.out.println(followRelationship);
    }


    @Test
    void copyUtilTest() {
        Post post = new Post(1, new Date(), "ilist", "desc", 0, 0, "title");
        PostBrief copy = CopyUtil.copy(post, PostBrief.class);
        System.out.println(copy);
    }

    @Test
    void password() {
        String pass = "123456";
        String encode = passwordEncoder.encode(pass);
        System.out.println(encode);
    }

//    @Test
//    void t(){
//        List<Integer> recommend = similarityUtil.recommend(3);
//        for (Integer integer : recommend) {
//            System.out.println(integer);
//        }
//    }

    @Test
    void recommendPlaces(){
//        List<PlaceBiref> places = placeService.recommendPlaces(1);
//        for (PlaceBiref place:places){
//            System.out.println(place.getPlaceId().toString());
//            System.out.println(place.getPlaceAddress());
//        }
        HashMap<String, Object> sd = searchService.search("哈", 0, 1);
        Object user = sd.get("post");
        System.out.println(user);
    }

    @Test
    public void test() {
        // 对应配置文件中配置的加密密钥
        System.setProperty("jasypt.encryptor.password", "tripmap");
        StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());
        System.out.println("加密mysql.username： " + stringEncryptor.encrypt("root"));
        System.out.println("加密mysql.password： " + stringEncryptor.encrypt("123456"));
        System.out.println("加密mysq.url： " + stringEncryptor.encrypt("jdbc:mysql://127.0.0.1:3306/trip_map?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false"));
        System.out.println("加密neo4j.username： " + stringEncryptor.encrypt("neo4j"));
        System.out.println("加密neo4j.password： " + stringEncryptor.encrypt("123456"));
        System.out.println("加密redis.host： " + stringEncryptor.encrypt("127.0.0.1"));
        System.out.println("加密cos.url： " + stringEncryptor.encrypt("https://juzimang-image-1307651200.cos.ap-shanghai.myqcloud.com"));
        System.out.println("加密cos.appId： " + stringEncryptor.encrypt("1307651200"));
        System.out.println("加密cos.secretId： " + stringEncryptor.encrypt("AKIDsWu2tTmoa5JFvJQGp78sPfeMnxqRlpc8"));
        System.out.println("加密cos.secretKey： " + stringEncryptor.encrypt("GH2WjfKiqhIbdOs50e2A3kESP5UdU4NP"));
    }

}
