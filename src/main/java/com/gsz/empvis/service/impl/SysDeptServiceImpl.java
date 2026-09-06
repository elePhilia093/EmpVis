package com.gsz.empvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gsz.empvis.dto.dept.DeptAddDTO;
import com.gsz.empvis.dto.dept.DeptQueryDTO;
import com.gsz.empvis.dto.dept.DeptUpdateDTO;
import com.gsz.empvis.entity.SysDept;
import com.gsz.empvis.exception.BusinessException;
import com.gsz.empvis.mapper.SysDeptMapper;
import com.gsz.empvis.service.SysDeptService;
import com.gsz.empvis.vo.dept.SysDeptVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    public SysDeptServiceImpl(SysDeptMapper sysDeptMapper) {
        this.sysDeptMapper = sysDeptMapper;
    }

    /**
     * 部门查询
     *
     * 无查询条件：返回树形结构
     * 有查询条件：返回平铺结果
     */
    @Override
    public List<SysDeptVO> list(DeptQueryDTO queryDTO) {

        LambdaQueryWrapper<SysDept> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.like(
                StringUtils.hasText(queryDTO.getDeptName()),
                SysDept::getDeptName,
                queryDTO.getDeptName()
        );

        wrapper.eq(
                queryDTO.getStatus() != null,
                SysDept::getStatus,
                queryDTO.getStatus()
        );

        wrapper.orderByAsc(SysDept::getSortOrder);

        List<SysDept> deptList =
                sysDeptMapper.selectList(wrapper);

        List<SysDeptVO> voList =
                deptList.stream()
                        .map(this::toVO)
                        .toList();

        // 存在查询条件时，直接平铺
        if (StringUtils.hasText(queryDTO.getDeptName())
                || queryDTO.getStatus() != null) {

            return voList;
        }

        // 无查询条件时构建部门树
        return buildTree(voList);
    }

    /**
     * 构建部门树
     */
    private List<SysDeptVO> buildTree(List<SysDeptVO> list) {

        Map<Long, SysDeptVO> map = new LinkedHashMap<>();

        for (SysDeptVO dept : list) {
            dept.setChildren(new ArrayList<>());
            map.put(dept.getId(), dept);
        }

        List<SysDeptVO> roots = new ArrayList<>();

        for (SysDeptVO dept : list) {

            Long parentId = dept.getParentId();

            // 根部门：parentId 为 null 或 0
            if (parentId == null || parentId == 0) {
                roots.add(dept);
                continue;
            }

            SysDeptVO parent = map.get(parentId);

            if (parent != null) {
                parent.getChildren().add(dept);
            }
        }

        return roots;
    }

    /**
     * Entity 转 VO
     */
    private SysDeptVO toVO(SysDept dept) {

        SysDeptVO vo = new SysDeptVO();

        vo.setId(dept.getId());
        vo.setParentId(dept.getParentId());
        vo.setDeptCode(dept.getDeptCode());
        vo.setDeptName(dept.getDeptName());
        vo.setLeaderId(dept.getLeaderId());
        vo.setSortOrder(dept.getSortOrder());
        vo.setStatus(dept.getStatus());
        vo.setRemark(dept.getRemark());
        vo.setCreateTime(dept.getCreateTime());

        return vo;
    }

    /**
     * 新增子部门
     */
    @Override
    public void add(DeptAddDTO addDTO) {
        System.out.println(addDTO.getParentId());
        // 必须指定父部门
        if (addDTO.getParentId() == null
                || addDTO.getParentId() == 0) {

            throw new BusinessException("请选择上级部门");
        }

        // 检查父部门是否存在
        SysDept parentDept =
                sysDeptMapper.selectById(addDTO.getParentId());

        if (parentDept == null) {
            throw new BusinessException("上级部门不存在");
        }

        // 检查部门编码是否重复
        LambdaQueryWrapper<SysDept> codeWrapper =
                new LambdaQueryWrapper<>();

        codeWrapper.eq(
                SysDept::getDeptCode,
                addDTO.getDeptCode()
        );

        Long codeCount =
                sysDeptMapper.selectCount(codeWrapper);

        if (codeCount > 0) {
            throw new BusinessException("部门编码已存在");
        }

        // 新增部门
        SysDept dept = new SysDept();

        dept.setParentId(addDTO.getParentId());
        dept.setDeptCode(addDTO.getDeptCode());
        dept.setDeptName(addDTO.getDeptName());
        dept.setLeaderId(addDTO.getLeaderId());
        dept.setSortOrder(addDTO.getSortOrder());
        dept.setStatus(addDTO.getStatus());
        dept.setRemark(addDTO.getRemark());

        sysDeptMapper.insert(dept);
    }

    /**
     * 修改部门
     */
    @Override
    public void update(DeptUpdateDTO updateDTO) {

        SysDept dept =
                sysDeptMapper.selectById(updateDTO.getId());

        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查部门编码是否与其他部门重复
        if (StringUtils.hasText(updateDTO.getDeptCode())) {

            LambdaQueryWrapper<SysDept> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(
                    SysDept::getDeptCode,
                    updateDTO.getDeptCode()
            );

            wrapper.ne(
                    SysDept::getId,
                    updateDTO.getId()
            );

            Long count =
                    sysDeptMapper.selectCount(wrapper);

            if (count > 0) {
                throw new BusinessException("部门编码已存在");
            }

            dept.setDeptCode(updateDTO.getDeptCode());
        }

        if (StringUtils.hasText(updateDTO.getDeptName())) {
            dept.setDeptName(updateDTO.getDeptName());
        }

        if (updateDTO.getLeaderId() != null) {
            dept.setLeaderId(updateDTO.getLeaderId());
        }

        if (updateDTO.getSortOrder() != null) {
            dept.setSortOrder(updateDTO.getSortOrder());
        }

        if (updateDTO.getStatus() != null) {
            dept.setStatus(updateDTO.getStatus());
        }

        dept.setRemark(updateDTO.getRemark());

        sysDeptMapper.updateById(dept);
    }

    /**
     * 删除部门
     */
    @Override
    public void delete(Long id) {

        SysDept dept =
                sysDeptMapper.selectById(id);

        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 存在子部门时不允许删除
        LambdaQueryWrapper<SysDept> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(
                SysDept::getParentId,
                id
        );

        Long childCount =
                sysDeptMapper.selectCount(wrapper);

        if (childCount > 0) {
            throw new BusinessException("该部门存在子部门，无法删除");
        }

        sysDeptMapper.deleteById(id);
    }
}