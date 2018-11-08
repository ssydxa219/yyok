package com.yyokay.share.util.aaa.client.config.custom;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @description: 认证管理
 * @author: linqinghong
 * ?* @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        Map data;
        if(authentication.getDetails() instanceof Map) {
            data = (Map) authentication.getDetails();
        }else{
            return null;
        }
        String clientId = (String) data.get("client");
        Assert.hasText(clientId, "clientId must have value");

        String password = (String) authentication.getCredentials();

        CustomUserDetails customUserDetails = checkUsernameAndPassword(username, password);
        customUserDetails.setClientId(clientId);
        if (customUserDetails == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        return new CustomAuthenticationToken(customUserDetails);
    }

    private CustomUserDetails checkUsernameAndPassword(String username, String password) {
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !userDetails.getPassword().equals(password)) {
            return null;
        }
        return userDetails;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}