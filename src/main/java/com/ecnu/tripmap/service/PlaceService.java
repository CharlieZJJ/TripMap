package com.ecnu.tripmap.service;

import com.ecnu.tripmap.model.vo.PlaceBiref;
import com.ecnu.tripmap.result.Response;

import java.util.List;

public interface PlaceService {
    Response collectPlace(Integer user_id, Integer place_id);
    Response cancelCollectPlace(Integer user_id,Integer place_id);
    List<PlaceBiref> recommendPlaces(Integer user_id);
}
