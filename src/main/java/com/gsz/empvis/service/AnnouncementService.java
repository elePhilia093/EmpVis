package com.gsz.empvis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.dto.announcement.AnnouncementAddDTO;
import com.gsz.empvis.dto.announcement.AnnouncementQueryDTO;
import com.gsz.empvis.dto.announcement.AnnouncementUpdateDTO;
import com.gsz.empvis.vo.announcement.AnnouncementVO;

public interface AnnouncementService {

    /**
     * 分页查询公告
     *
     * @param queryDTO 查询条件
     * @param admin 是否为管理员
     */
    IPage<AnnouncementVO> page(
            AnnouncementQueryDTO queryDTO,
            boolean admin
    );

    /**
     * 新增公告
     *
     * @param addDTO 公告数据
     * @param publisherId 当前登录用户ID
     */
    void add(
            AnnouncementAddDTO addDTO,
            Long publisherId
    );

    /**
     * 修改公告
     */
    void update(
            AnnouncementUpdateDTO updateDTO
    );

    /**
     * 删除公告
     */
    void delete(Long id);
}