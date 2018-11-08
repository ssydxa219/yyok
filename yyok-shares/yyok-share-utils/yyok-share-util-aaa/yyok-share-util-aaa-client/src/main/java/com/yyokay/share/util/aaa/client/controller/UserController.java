package com.yyokay.share.util.aaa.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
/**
 * @author: linqinghong
 * ?* @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@RestController
public class UserController {

    @GetMapping("/user")
    public Principal user(Principal user){
        return user;
    }

}
