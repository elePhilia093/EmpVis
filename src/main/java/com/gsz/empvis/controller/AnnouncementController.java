package com.gsz.empvis.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.common.Result;
import com.gsz.empvis.dto.announcement.AnnouncementAddDTO;
import com.gsz.empvis.dto.announcement.AnnouncementQueryDTO;
import com.gsz.empvis.dto.announcement.AnnouncementUpdateDTO;
import com.gsz.empvis.security.LoginUser;
import com.gsz.empvis.service.AnnouncementService;
import com.gsz.empvis.vo.announcement.AnnouncementVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(
            AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 分页查询公告
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:announcement:list')")
    public Result<IPage<AnnouncementVO>> list(
            AnnouncementQueryDTO queryDTO,
            Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                "ROLE_ADMIN".equals(
                                        authority.getAuthority()
                                )
                        );

        return Result.success(
                announcementService.page(
                        queryDTO,
                        isAdmin
                )
        );
    }

    /**
     * 新增公告
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:announcement:add')")
    public Result<Void> add(
            @Valid @RequestBody
            AnnouncementAddDTO addDTO,
            Authentication authentication) {

        LoginUser loginUser =
                (LoginUser) authentication.getPrincipal();

        Long publisherId =
                loginUser.getUser().getId();

        announcementService.add(
                addDTO,
                publisherId
        );

        return Result.success(null);
    }

    /**
     * 修改公告
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:announcement:update')")
    public Result<Void> update(
            @Valid @RequestBody
            AnnouncementUpdateDTO updateDTO) {

        announcementService.update(
                updateDTO
        );

        return Result.success(null);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:announcement:delete')")
    public Result<Void> delete(
            @PathVariable Long id) {

        announcementService.delete(id);

        return Result.success(null);
    }
}