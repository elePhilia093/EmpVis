package com.gsz.empvis.service;

import com.gsz.empvis.dto.personal.PasswordUpdateDTO;
import com.gsz.empvis.dto.personal.PersonalInfoUpdateDTO;
import com.gsz.empvis.vo.personal.PersonalInfoVO;

public interface PersonalService {

    /**
     * 获取当前用户个人信息
     */
    PersonalInfoVO getInfo(Long userId);

    /**
     * 修改当前用户个人信息
     */
    void updateInfo(
            Long userId,
            PersonalInfoUpdateDTO updateDTO
    );

    /**
     * 修改当前用户密码
     */
    void updatePassword(
            Long userId,
            PasswordUpdateDTO updateDTO
    );
}