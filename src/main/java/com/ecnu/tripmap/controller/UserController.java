package com.ecnu.tripmap.controller;

import com.ecnu.tripmap.model.vo.*;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.result.Response;
import com.ecnu.tripmap.result.ResponseStatus;
import com.ecnu.tripmap.service.Impl.PostServiceImpl;
import com.ecnu.tripmap.service.Impl.UserServiceImpl;
import com.ecnu.tripmap.utils.CopyUtil;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@Api(tags = "user")
public class UserController {

    @Resource
    private HttpSession session;

    @Resource
    private UserServiceImpl userService;


    @ApiOperation(value = "登录，若session之中已经有用户的信息，那么直接放行")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -2, message = "账号或密码不正确")
    })
    @PostMapping("/login")
    public Response login(@RequestBody @Valid @ApiParam(name = "user", value = "传入账号和密码即可") User user) {
        Response response = userService.login(user);
        if (response.getCode() == 0) {
            UserVo copy = CopyUtil.copy(response.getData(), UserVo.class);
            session.setAttribute("user", copy);
        }
        return response;
    }

    @ApiOperation(value = "注册，成功后会写入session")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -3, message = "账号已经存在"),
            @ApiResponse(code = -4, message = "注册失败，请联系工作人员")
    })
    @PostMapping("/register")
    public Response register(@RequestBody @Valid @ApiParam(name = "user", value = "必须传入的有账号和密码，昵称如未传入将设置为随机8位字符串，头像设置为默认头像") User user) {
        Response register = userService.register(user);
        if (register.getCode() == 0) {
            session.setAttribute("user", register.getData());
        }
        return register;
    }


    // TODO
    @ApiOperation(value = "查询用户信息，首先查看是否是当前用户，是的话就从session拿到，否则查询数据库")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -5, message = "用户不存在")
    })
    @GetMapping("/{user_id}")
    public Response info(@ApiParam(name = "user_id", value = "路径参数，要查找的id") @PathVariable(name = "user_id") Integer user_id) {
        UserVo user = (UserVo) session.getAttribute("user");
        if (Objects.equals(user.getUserId(), user_id)) {
            return Response.success(user);
        }

        UserVo userInfo = userService.findUserInfo(user_id);
        if (userInfo == null)
            return Response.status(ResponseStatus.USER_NOT_EXIST);
        return Response.success(userInfo);
    }



    @ApiOperation(value = "查询某个用户收藏的地点的列表")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/{user_id}/address/list")
    public Response addressList(@ApiParam(name = "user_id", value = "路径参数，要查询的用户的id") @PathVariable(name = "user_id") Integer user_id) {
        List<PlaceVo> placeVos = userService.findUserStoredPlace(user_id);
        return Response.success(placeVos);
    }

    @ApiOperation(value = "查询某个用户的关注列表")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/{user_id}/follow/list")
    public Response followList(@ApiParam(name = "user_id", value = "路径参数，要查询的用户的id") @PathVariable(name = "user_id") Integer user_id) {
        List<UserBrief> userVos = userService.findUserFollowedUser(user_id);
        return Response.success(userVos);
    }

    @ApiOperation(value = "查询某个用户的粉丝列表")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/{user_id}/fan/list")
    public Response fanList(@ApiParam(name = "user_id", value = "路径参数，要查询的用户的id") @PathVariable(name = "user_id") Integer user_id) {
        List<UserBrief> userVos = userService.findUserFanUser(user_id);
        return Response.success(userVos);
    }

    @ApiOperation(value = "查询用户收藏的帖子")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/{user_id}/post/collect/list")
    public Response postList(@ApiParam(name = "user_id", value = "路径参数，要查询的用户的id") @PathVariable(name = "user_id") Integer user_id) {
        List<PostBrief> posts = userService.findCollectPostList(user_id);
        return Response.success(posts);
    }

    @ApiOperation(value = "查询用户发布的帖子")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/{user_id}/post/list")
    public Response ReleasePostList(@ApiParam(name = "user_id", value = "路径参数，要查询的用户的id") @PathVariable(name = "user_id") Integer user_id) {
        List<PostBrief> posts = userService.findPublishPostList(user_id);
        return Response.success(posts);
    }

    // TODO
    @ApiOperation(value = "关注用户，需传入要关注的用户id")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = -6, message = "尚未登录"),
            @ApiResponse(code = -5, message = "用户不存在")
    })
    @PutMapping("/follow/{follow_id}")
    public Response followUser(@ApiParam(name = "follow_id", value = "需要关注的用户的id") @PathVariable(name = "follow_id") Integer follow_id) {
        UserVo user = (UserVo) session.getAttribute("user");
        return userService.followAUser(user.getUserId(), follow_id);
    }

    @ApiOperation(value="取消关注用户，需传入要取消关注的用户id")
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=-6,message = "尚未登录"),
            @ApiResponse(code=-5,message = "用户不存在")
    })
    @PutMapping("/cancel/follow/{follow_id}")
    public Response cancelFollowUser(@ApiParam(name = "follow_id",value = "需要取消关注的用户的id")@PathVariable(name = "follow_id") Integer follow_id){
        UserVo user=(UserVo) session.getAttribute("user");
        return userService.cancelFollowAUser(user.getUserId(), follow_id);
    }


    @ApiOperation(value="删除帖子，需要传入需要删除的帖子的id")
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=-6,message = "尚未登录"),
            @ApiResponse(code=-5,message = "用户不存在")
    })
    @PutMapping("/delete/post/{post_id}")
    public Response deleteAPost(@ApiParam(name = "post_id",value = "需要删除的帖子的id")@PathVariable(name = "post_id") Integer post_id){
        UserVo user=(UserVo) session.getAttribute("user");
        return userService.deleteAPost(user.getUserId(), post_id);
    }


}
