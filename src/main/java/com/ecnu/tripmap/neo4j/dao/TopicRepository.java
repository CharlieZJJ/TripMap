package com.ecnu.tripmap.neo4j.dao;

import com.ecnu.tripmap.neo4j.node.PostNode;
import com.ecnu.tripmap.neo4j.node.TopicNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends Neo4jRepository<TopicNode, Long> {

    @Query ("CREATE (t:Topic{topic_id : $topic_id, topic_name : $topic_name}) RETURN t")
    TopicNode createTopicNode(Integer topic_id, String topic_name);

    @Query("MATCH (p:Post) -[:BELONG]-> (t:Topic) WHERE p.post_id = $post_id RETURN (t)")
    List<TopicNode> findPostTopics(Integer post_id);

    @Query("MATCH (t:Topic) WHERE t.topic_name = $topic_name RETURN t")
    TopicNode fineTopicByName(String topic_name);

    @Query("MATCH (p:Post), (t:Topic) WHERE p.post_id = $post_id AND t.topic_id = $topic_id CREATE (p) -[b:BELONG]-> (t) RETURN id(b)")
    Integer createBelongRelationship(Integer post_id, Integer topic_id);

}
