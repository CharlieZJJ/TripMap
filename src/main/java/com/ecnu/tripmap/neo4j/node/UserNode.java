package com.ecnu.tripmap.neo4j.node;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Node("User")
@Data
@NoArgsConstructor
public class UserNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("user_id")
    private Integer userId;

    @Property("user_nickname")
    private String userNickname;

    @Property("user_avatar")
    private String userAvatar;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.INCOMING)
    private List<UserNode> follows = new ArrayList<>();


    public UserNode(Integer userId, String userNickname, String userAvatar) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userAvatar = userAvatar;
    }
}
