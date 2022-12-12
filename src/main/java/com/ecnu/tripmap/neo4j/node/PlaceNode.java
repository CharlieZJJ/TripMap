package com.ecnu.tripmap.neo4j.node;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Node("Place")
@NoArgsConstructor
public class PlaceNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("place_id")
    private Integer placeId;

    @Property("place_province")
    private String placeProvince;

    @Property("place_area")
    private String placeArea;

    @Property("place_address")
    private String placeAddress;

    @Relationship(type = "STORE", direction = Relationship.Direction.INCOMING)
    private List<PostNode> stores = new ArrayList<>();

    @Relationship(type = "SUGGEST", direction = Relationship.Direction.INCOMING)
    private List<PostNode> suggests = new ArrayList<>();

    public PlaceNode(Integer placeId, String placeProvince, String placeArea, String placeAddress) {
        this.placeId = placeId;
        this.placeProvince = placeProvince;
        this.placeArea = placeArea;
        this.placeAddress = placeAddress;
    }
}
