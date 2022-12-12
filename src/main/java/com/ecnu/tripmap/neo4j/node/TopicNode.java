package com.ecnu.tripmap.neo4j.node;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Node("Topic")
@Data
@NoArgsConstructor
public class TopicNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("topic_id")
    private Integer topicId;

    @Property("topic_name")
    private String topicName;

    @Relationship(type = "BELONG", direction = Relationship.Direction.INCOMING)
    private List<PostNode> belongs = new ArrayList<>();

    public TopicNode(Integer topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }
}
