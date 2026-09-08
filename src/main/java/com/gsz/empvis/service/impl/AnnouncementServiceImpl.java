package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.dto.announcement.AnnouncementAddDTO;
import com.gsz.empvis.dto.announcement.AnnouncementQueryDTO;
import com.gsz.empvis.dto.announcement.AnnouncementUpdateDTO;
import com.gsz.empvis.entity.SysAnnouncement;
import com.gsz.empvis.entity.SysUser;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.SysAnnouncementMapper;
import com.gsz.empvis.mapper.SysUserMapper;
import com.gsz.empvis.service.AnnouncementService;
import com.gsz.empvis.vo.announcement.AnnouncementVO;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
public class AnnouncementServiceImpl
        implements AnnouncementService {

    private final SysAnnouncementMapper sysAnnouncementMapper;

    private final SysUserMapper sysUserMapper;

    public AnnouncementServiceImpl(
            SysAnnouncementMapper sysAnnouncementMapper,
            SysUserMapper sysUserMapper) {

        this.sysAnnouncementMapper =
                sysAnnouncementMapper;

        this.sysUserMapper =
                sysUserMapper;
    }

    /**
     * 分页查询公告
     */
    @Override
    public IPage<AnnouncementVO> page(
            AnnouncementQueryDTO queryDTO,
            boolean admin) {

        Page<SysAnnouncement> page =
                new Page<>(
                        queryDTO.getCurrent(),
                        queryDTO.getSize()
                );

        LambdaQueryWrapper<SysAnnouncement> wrapper =
                new LambdaQueryWrapper<>();

        /*
         * 标题模糊查询
         */
        wrapper.like(
                StringUtils.hasText(
                        queryDTO.getTitle()
                ),
                SysAnnouncement::getTitle,
                queryDTO.getTitle()
        );

        /*
         * 管理员可以查看全部状态
         *
         * 普通员工、主管只能查看已发布公告
         */
        if (admin) {

            wrapper.eq(
                    queryDTO.getPublishStatus() != null,
                    SysAnnouncement::getPublishStatus,
                    queryDTO.getPublishStatus()
            );

        } else {

            wrapper.eq(
                    SysAnnouncement::getPublishStatus,
                    1
            );
        }

        /*
         * 置顶公告优先
         */
        wrapper.orderByDesc(
                SysAnnouncement::getIsTop
        );

        /*
         * 最新公告优先
         */
        wrapper.orderByDesc(
                SysAnnouncement::getPublishTime
        );

        wrapper.orderByDesc(
                SysAnnouncement::getCreateTime
        );

        IPage<SysAnnouncement> result =
                sysAnnouncementMapper.selectPage(
                        page,
                        wrapper
                );

        List<AnnouncementVO> records =
                result.getRecords()
                        .stream()
                        .map(this::toVO)
                        .toList();

        /*
         * 补充发布人姓名
         */
        fillPublisherName(records);

        Page<AnnouncementVO> voPage =
                new Page<>(
                        result.getCurrent(),
                        result.getSize(),
                        result.getTotal()
                );

        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 新增公告
     */
    @Override
    @Transactional
    public void add(
            AnnouncementAddDTO addDTO,
            Long publisherId) {

        /*
         * 检查发布人
         */
        SysUser publisher =
                sysUserMapper.selectById(
                        publisherId
                );

        if (publisher == null) {

            throw new BusinessException(
                    "发布人用户不存在"
            );
        }

        SysAnnouncement announcement =
                new SysAnnouncement();

        announcement.setTitle(
                addDTO.getTitle()
        );

        announcement.setContent(
                addDTO.getContent()
        );

        /*
         * 发布人由当前登录用户确定
         * 不从前端接收
         */
        announcement.setPublisherId(
                publisherId
        );

        Integer publishStatus =
                addDTO.getPublishStatus();

        if (publishStatus == null) {
            publishStatus = 0;
        }

        announcement.setPublishStatus(
                publishStatus
        );

        Integer isTop =
                addDTO.getIsTop();

        if (isTop == null) {
            isTop = 0;
        }

        announcement.setIsTop(isTop);

        /*
         * 已发布 → 自动记录发布时间
         */
        if (Objects.equals(
                publishStatus,
                1)) {

            announcement.setPublishTime(
                    LocalDateTime.now()
            );

        } else {

            announcement.setPublishTime(null);
        }

        announcement.setCreateTime(
                LocalDateTime.now()
        );

        announcement.setUpdateTime(
                LocalDateTime.now()
        );

        /*
         * deleted 交给逻辑删除字段控制
         */
        announcement.setDeleted(0);

        sysAnnouncementMapper.insert(
                announcement
        );
    }

    /**
     * 修改公告
     */
    @Override
    @Transactional
    public void update(
            AnnouncementUpdateDTO updateDTO) {

        SysAnnouncement announcement =
                sysAnnouncementMapper.selectById(
                        updateDTO.getId()
                );

        if (announcement == null) {

            throw new BusinessException(
                    "公告不存在"
            );
        }

        announcement.setTitle(
                updateDTO.getTitle()
        );

        announcement.setContent(
                updateDTO.getContent()
        );

        Integer publishStatus =
                updateDTO.getPublishStatus();

        if (publishStatus == null) {
            publishStatus = 0;
        }

        announcement.setPublishStatus(
                publishStatus
        );

        Integer isTop =
                updateDTO.getIsTop();

        if (isTop == null) {
            isTop = 0;
        }

        announcement.setIsTop(isTop);

        /*
         * 发布状态发生变化时处理发布时间
         *
         * 0 → 1：记录当前发布时间
         * 1 → 1：保留原发布时间
         * 1 → 0：清空发布时间
         */
        if (Objects.equals(
                publishStatus,
                1)) {

            if (announcement.getPublishTime()
                    == null) {

                announcement.setPublishTime(
                        LocalDateTime.now()
                );
            }

        } else {

            announcement.setPublishTime(null);
        }

        announcement.setUpdateTime(
                LocalDateTime.now()
        );

        sysAnnouncementMapper.updateById(
                announcement
        );
    }

    /**
     * 删除公告
     */
    @Override
    @Transactional
    public void delete(Long id) {

        SysAnnouncement announcement =
                sysAnnouncementMapper.selectById(id);

        if (announcement == null) {

            throw new BusinessException(
                    "公告不存在"
            );
        }

        sysAnnouncementMapper.deleteById(id);
    }

    /**
     * Entity 转 VO
     */
    private AnnouncementVO toVO(
            SysAnnouncement announcement) {

        AnnouncementVO vo =
                new AnnouncementVO();

        vo.setId(
                announcement.getId()
        );

        vo.setTitle(
                announcement.getTitle()
        );

        vo.setContent(
                announcement.getContent()
        );

        vo.setPublisherId(
                announcement.getPublisherId()
        );

        vo.setPublishStatus(
                announcement.getPublishStatus()
        );

        vo.setPublishTime(
                announcement.getPublishTime()
        );

        vo.setIsTop(
                announcement.getIsTop()
        );

        vo.setCreateTime(
                announcement.getCreateTime()
        );

        vo.setUpdateTime(
                announcement.getUpdateTime()
        );

        return vo;
    }

    /**
     * 批量补充发布人姓名
     */
    private void fillPublisherName(
            List<AnnouncementVO> records) {

        if (records.isEmpty()) {
            return;
        }

        List<Long> publisherIds =
                records.stream()
                        .map(
                                AnnouncementVO::getPublisherId
                        )
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (publisherIds.isEmpty()) {
            return;
        }

        List<SysUser> users =
                sysUserMapper.selectBatchIds(
                        publisherIds
                );

        Map<Long, String> publisherNameMap =
                users.stream()
                        .collect(
                                Collectors.toMap(
                                        SysUser::getId,
                                        SysUser::getUsername
                                )
                        );

        records.forEach(vo ->
                vo.setPublisherName(
                        publisherNameMap.get(
                                vo.getPublisherId()
                        )
                )
        );
    }
}