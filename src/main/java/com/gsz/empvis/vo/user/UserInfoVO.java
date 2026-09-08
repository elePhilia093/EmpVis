package com.gsz.empvis.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfoVO {

    private Long userId;

    private String username;

    private List<String> roles;

}