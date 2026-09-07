package com.gsz.empvis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gsz.empvis.dto.leave.LeaveAddDTO;
import com.gsz.empvis.dto.leave.LeaveAuditDTO;
import com.gsz.empvis.dto.leave.LeaveQueryDTO;
import com.gsz.empvis.vo.leave.LeaveVO;

public interface EmpLeaveService {

    /**
     * 分页查询请假记录
     */
    IPage<LeaveVO> page(LeaveQueryDTO queryDTO, Long userId);

    /**
     * 提交请假申请
     */
    void add(LeaveAddDTO addDTO, Long userId);

    /**
     * 审批请假
     */
    void audit(LeaveAuditDTO auditDTO, Long approverUserId);
}