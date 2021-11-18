package com.dedalusin.webgate.controller;

import com.dedalusin.webgate.Balance.ImLoadBalance;
import entity.ImNode;
import entity.LoginResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import util.JsonUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 用户登录验证，返回netty node和token userid便于netty进行鉴权，这里all in
 */
@RestController
@RequestMapping(value = "/user", produces = "MediaType.APPLICATION_JSON_VALUE"+";charset=utf-8")
public class UserController {

    @Resource
    private ImLoadBalance imLoadBalance;

    @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.GET)
    public String login(
            @PathVariable("username") String username,
            @PathVariable("passsword") String password
    ) {
        List<ImNode> nodes = imLoadBalance.getWorkers();
        Collections.sort(nodes);
        LoginResponse loginResponse = LoginResponse.builder().userId(username).token(username).imNode(nodes.get(0)).build();
        return JsonUtil.pojoToJson(loginResponse);
    }
}
