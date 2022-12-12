package com.ecnu.tripmap.controller;


import com.ecnu.tripmap.model.vo.PlaceBiref;
import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.neo4j.dao.PlaceRepository;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.result.ResponseStatus;
import com.ecnu.tripmap.service.PlaceService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/address")
@Api(tags = "address")
public class PlaceController {
    @Resource
    private HttpSession session;

    @Resource
    private PlaceService placeService;

    @Resource
    private PlaceRepository placeRepository;

    //TODO
    @ApiOperation(value = "获取地点推荐，无传入参数")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -11, message = "地点推荐失败，请联系工作人员")
    })
    @GetMapping("recommend")
    public Response recommendPlaces() {
        UserVo user = (UserVo) session.getAttribute("user");
        List<PlaceBiref> places = placeService.recommendPlaces(user.getUserId());
        return Response.success(places);
    }


    //TODO
    @ApiOperation(value = "收藏一个地点，需要传入地点id")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -6, message = "尚未登录"),
            @ApiResponse(code = -8, message = "地点不存在")
    })
    @PutMapping("{address_id}/collect")
    public Response collectPlace(@ApiParam(name = "place_id", value = "要收藏的地点id") @PathVariable Integer address_id) {
        UserVo user = (UserVo) session.getAttribute("user");
        return placeService.collectPlace(user.getUserId(), address_id);
    }

    @ApiOperation(value = "取消收藏一个地点，需要传入地点id")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -6, message = "尚未登录"),
            @ApiResponse(code = -8, message = "地点不存在")
    })
    @PutMapping("/cancel/{address_id}/collect")
    public Response cancelCollectPlace(@ApiParam(name = "place_id",value = "要取消收藏的地点id")@PathVariable Integer address_id){
        UserVo user =(UserVo) session.getAttribute("user");
        return placeService.cancelCollectPlace(user.getUserId(),address_id);
    }

}
