package com.ecnu.tripmap.neo4j.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Node("Post")
@NoArgsConstructor
public class PostNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("post_id")
    private Integer postId;

    @Relationship(type = "PUBLISH", direction = Relationship.Direction.INCOMING)
    private List<UserNode> publisher = new ArrayList<>();

    @Relationship(type = "LIKE", direction = Relationship.Direction.INCOMING)
    private List<UserNode> likes = new ArrayList<>();

    @Relationship(type = "COLLECT", direction = Relationship.Direction.INCOMING)
    private List<UserNode> collects = new ArrayList<>();


    public PostNode(Integer postId) {
        this.postId = postId;
    }
}
