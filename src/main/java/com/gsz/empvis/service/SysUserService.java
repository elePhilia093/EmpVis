package com.gsz.empvis.service;

import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.dto.user.UserAddDTO;
import com.gsz.empvis.dto.user.UserQueryDTO;
import com.gsz.empvis.dto.user.UserUpdateDTO;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.vo.user.LoginVO;
import com.gsz.empvis.vo.user.UserInfoVO;
import com.gsz.empvis.vo.user.UserVO;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SysUserService {

    SysUser getByUsername(String username);

    LoginVO login(String username, String password);

    Collection<? extends GrantedAuthority> getAuthoritiesByUserId(Long userId);

    PageResult<UserVO> page(UserQueryDTO queryDTO);

    void add(UserAddDTO addDTO);

    void update(UserUpdateDTO updateDTO);

    void delete(Long id);

    UserInfoVO getUserInfo(Long userId);
}