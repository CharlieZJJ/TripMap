package com.ecnu.tripmap.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBiref {
    private Integer placeId;

    private String placeAddress;
}
