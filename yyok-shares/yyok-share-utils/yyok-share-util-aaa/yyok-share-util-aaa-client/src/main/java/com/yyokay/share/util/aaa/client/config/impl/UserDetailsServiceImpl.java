package com.yyokay.share.util.aaa.client.config.impl;

import com.yyokay.share.util.aaa.client.config.custom.CustomUserDetails;
import com.yyokay.share.util.aaa.client.config.service.JdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @description:
 * @author: linqinghong
 * ?* @date: Created in 2018/11/08
 * @modified By:
 * @version: 1.0.1
 **/
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    JdbcUserDetailsService jdbcUserDetailsService;

    /**
     * 根据用户名获取登录用户信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails customUserDetails = jdbcUserDetailsService.loadUserDetailsByUserName(username);
        if(customUserDetails == null){
            throw new UsernameNotFoundException("用户名："+ username + "不存在！");
        }
        customUserDetails.setRoles(jdbcUserDetailsService.loadUserRolesByUserId(Long.valueOf(customUserDetails.getUserId())));
        return customUserDetails;
    }
}
