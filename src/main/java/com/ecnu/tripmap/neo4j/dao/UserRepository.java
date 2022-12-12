package com.ecnu.tripmap.neo4j.dao;

import com.ecnu.tripmap.neo4j.node.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends Neo4jRepository<UserNode, Long> {

    @Override
    <S extends UserNode> S save(S entity);

    @Query("MATCH (u:User) -[:PUBLISH]-> (p:Post) WHERE p.post_id = $post_id RETURN (u)")
    UserNode findPublisher(Integer post_id);

    @Query("MATCH (u:User)  WHERE u.user_id = $user_id RETURN (u)")
    UserNode fineUserById(Integer user_id);

    @Query("MATCH (u1:User) -[:FOLLOW]-> (u2:User) WHERE u1.user_id = $user_id RETURN(u2)")
    List<UserNode> findUserFollowedUser(Integer user_id);

    @Query("MATCH (u1:User) -[:FOLLOW]-> (u2:User) WHERE u2.user_id = $user_id RETURN(u1)")
    List<UserNode> findUserFanUser(Integer user_id);

    @Query("MATCH (u1:User), (u2:User) WHERE u1.user_id = $user_id AND u2.user_id = $follow_id CREATE (u1) -[f:FOLLOW]-> (u2) RETURN id(f)")
    Integer createFollowRelationship(Integer user_id, Integer follow_id);

    @Query("MATCH (u1:User)-[f:FOLLOW]->(u2:User) WHERE u1.user_id=$user_id AND u2.user_id=$follow_id DELETE f")
    void cancelFollowRelationship(Integer user_id,Integer follow_id);

    @Query("MATCH (u1:User) -[f:FOLLOW]-> (u2:User) WHERE u1.user_id = $user_id AND u2.user_id = $follow_id RETURN id(f)")
    Integer isFollowed(Integer user_id, Integer follow_id);


    @Query("MATCH (u:User)-[f:PUBLISH]->(p:Post) WHERE u.user_id=$user_id AND p.post_id=$post_id DETACH DELETE p")
    void deleteAPost(Integer user_id, Integer post_id);
}
