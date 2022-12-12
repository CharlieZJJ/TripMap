package com.ecnu.tripmap.neo4j.dao;

import com.ecnu.tripmap.neo4j.node.PostNode;
import com.ecnu.tripmap.neo4j.node.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends Neo4jRepository<PostNode, Long> {

    @Query("CREATE (p:Post{post_id : $post_id}) RETURN p")
    PostNode createPostNode(Integer post_id);

    @Query("MATCH (u:User) -[:COLLECT]-> (p:Post) WHERE u.user_id = $user_id RETURN (p)")
    List<PostNode> findUserCollectedPost(Integer user_id);

    @Query("MATCH (u:User) -[:PUBLISH]-> (p:Post) WHERE u.user_id = $user_id RETURN (p)")
    List<PostNode> findPublishPostList(Integer user_id);

    @Query("MATCH (po:Post) -[:SUGGEST]-> (pa:Place) WHERE pa.place_id = $place_id RETURN (po)")
    List<PostNode> findPlacePostList(Integer place_id);


    @Query("MATCH (u:User), (p:Post) WHERE u.user_id = $user_id AND p.post_id = $post_id CREATE (u) -[c:COLLECT]-> (p) RETURN id(c)")
    Integer createCollectRelationship(Integer user_id, Integer post_id);

    @Query("MATCH (u:User), (p:Post) WHERE u.user_id = $user_id AND p.post_id = $post_id CREATE (u) -[l:LIKE]-> (p) RETURN id(l)")
    Integer createLikeRelationship(Integer user_id, Integer post_id);

    @Query("MATCH (u:User), (p:Post) WHERE u.user_id = $user_id AND p.post_id = $post_id CREATE (u) -[pu:PUBLISH]-> (p) RETURN id(pu)")
    Integer createPublishRelationship(Integer post_id,Integer user_id);

    @Query("MATCH (u:User) -[l:LIKE]-> (p:Post) WHERE u.user_id = $user_id AND p.post_id = $post_id RETURN id(l)")
    Integer isLiked(Integer user_id, Integer post_id);

    @Query("MATCH (u:User) -[c:COLLECT]-> (p:Post) WHERE u.user_id = $user_id AND p.post_id = $post_id RETURN id(c)")
    Integer isCollected(Integer user_id, Integer post_id);

    @Query("MATCH (u:User) -[:LIKE]-> (p:Post) WHERE u.user_id = $user_id RETURN p")
    List<PostNode> findUserLikedPost(Integer user_id);

    @Query("MATCH (u:User)-[c:COLLECT]->(p:Post) WHERE u.user_id=$user_id AND p.post_id=$post_id DELETE c")
    void cancelPostCollect(Integer user_id, Integer post_id);

    @Query("MATCH (u:User)-[l:LIKE]->(p:Post) WHERE u.user_id=$user_id AND p.post_id=$post_id DELETE l")
    void cancelLikePost(Integer user_id, Integer post_id);

    @Query("MATCH (u:User) -[c:STORE]-> (p:Place) WHERE u.user_id = $user_id AND p.place_id = $place_id RETURN id(c)")
    Integer isPlaceCollected(Integer user_id, Integer place_id);
}
