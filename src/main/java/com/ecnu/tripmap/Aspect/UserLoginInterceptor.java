package com.ecnu.tripmap.Aspect;

import cn.hutool.json.JSONObject;
import com.ecnu.tripmap.model.vo.UserVo;
import com.ecnu.tripmap.mysql.entity.User;
import com.ecnu.tripmap.result.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            HttpSession session = request.getSession();
            UserVo user = (UserVo) session.getAttribute("user");
            if (user == null)
            {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                JSONObject entries = new JSONObject();
                entries.set("code", ResponseStatus.NOT_LOGIN.getCode());
                entries.set("message", ResponseStatus.NOT_LOGIN.getMessage());
                entries.set("data", "");
                writer.write(entries.toString());
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return true;
    }


}
