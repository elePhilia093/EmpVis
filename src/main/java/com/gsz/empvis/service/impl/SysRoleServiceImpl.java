package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsz.empvis.common.PageResult;
import com.gsz.empvis.dto.role.RoleAddDTO;
import com.gsz.empvis.dto.role.RoleQueryDTO;
import com.gsz.empvis.dto.role.RoleUpdateDTO;
import com.gsz.empvis.entity.SysRole;
import com.gsz.empvis.entity.SysUserRole;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.SysRoleMapper;
import com.gsz.empvis.mapper.SysUserRoleMapper;
import com.gsz.empvis.service.SysRoleService;
import com.gsz.empvis.vo.role.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    public SysRoleServiceImpl(
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper) {

        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public List<RoleVO> listAll() {

        LambdaQueryWrapper<SysRole> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getId);

        return sysRoleMapper.selectList(wrapper)
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public PageResult<RoleVO> page(RoleQueryDTO queryDTO) {

        Page<SysRole> page = new Page<>(
                queryDTO.getCurrent(),
                queryDTO.getSize()
        );

        LambdaQueryWrapper<SysRole> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(queryDTO.getRoleName()),
                SysRole::getRoleName,
                queryDTO.getRoleName()
        );

        wrapper.like(
                StringUtils.hasText(queryDTO.getRoleCode()),
                SysRole::getRoleCode,
                queryDTO.getRoleCode()
        );

        wrapper.eq(
                queryDTO.getStatus() != null,
                SysRole::getStatus,
                queryDTO.getStatus()
        );

        wrapper.orderByDesc(SysRole::getCreateTime);

        Page<SysRole> result =
                sysRoleMapper.selectPage(page, wrapper);

        List<RoleVO> records = result.getRecords()
                .stream()
                .map(this::toVO)
                .toList();

        return new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize(),
                result.getPages()
        );
    }

    @Override
    public void add(RoleAddDTO addDTO) {

        LambdaQueryWrapper<SysRole> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                SysRole::getRoleCode,
                addDTO.getRoleCode()
        );

        Long count =
                sysRoleMapper.selectCount(wrapper);

        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();

        role.setRoleName(addDTO.getRoleName());
        role.setRoleCode(addDTO.getRoleCode());
        role.setStatus(addDTO.getStatus());
        role.setRemark(addDTO.getRemark());
        role.setDeleted(0);

        sysRoleMapper.insert(role);
    }

    @Override
    public void update(RoleUpdateDTO updateDTO) {

        SysRole role =
                sysRoleMapper.selectById(updateDTO.getId());

        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        if (StringUtils.hasText(updateDTO.getRoleCode())) {

            LambdaQueryWrapper<SysRole> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(
                    SysRole::getRoleCode,
                    updateDTO.getRoleCode()
            );

            wrapper.ne(
                    SysRole::getId,
                    updateDTO.getId()
            );

            Long count =
                    sysRoleMapper.selectCount(wrapper);

            if (count > 0) {
                throw new BusinessException("角色编码已存在");
            }

            role.setRoleCode(updateDTO.getRoleCode());
        }

        if (StringUtils.hasText(updateDTO.getRoleName())) {
            role.setRoleName(updateDTO.getRoleName());
        }

        role.setStatus(updateDTO.getStatus());
        role.setRemark(updateDTO.getRemark());

        sysRoleMapper.updateById(role);
    }

    @Override
    public void delete(Long id) {

        SysRole role =
                sysRoleMapper.selectById(id);

        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 判断角色是否已经分配给用户
        LambdaQueryWrapper<SysUserRole> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                SysUserRole::getRoleId,
                id
        );

        Long count =
                sysUserRoleMapper.selectCount(wrapper);

        if (count > 0) {
            throw new BusinessException(
                    "该角色已分配给用户，不能直接删除"
            );
        }

        sysRoleMapper.deleteById(id);
    }

    private RoleVO toVO(SysRole role) {

        RoleVO vo = new RoleVO();

        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());

        return vo;
    }
}