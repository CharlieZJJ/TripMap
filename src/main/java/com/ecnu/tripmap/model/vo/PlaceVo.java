package com.ecnu.tripmap.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceVo {
    private Integer placeId;

    private String placeProvince;

    private String placeArea;

    private String placeAddress;
}
