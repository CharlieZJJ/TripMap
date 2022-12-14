package com.ecnu.tripmap.controller;

import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.service.SearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
public class SearchController {

    @Resource
    private HttpSession session;
    @Resource
    private SearchService searchService;

    @GetMapping("/search")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @ApiOperation("传入查询内容和查询类型，返回一个hashmap，查询用户时会有一个key为user，地点是place，帖子是post")
    public Response search(@RequestParam(value = "query") @ApiParam(name = "query", value = "要查询的内容") String query,
                           @RequestParam(value = "type") @ApiParam(name = "type", value = "用户1 地点2 其余帖子") int type){
        UserVo user = (UserVo) session.getAttribute("user");
        HashMap<String, Object> search = searchService.search(query, type, user.getUserId());
        return Response.success(search);
    }
}
