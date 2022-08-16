package com.glx.xuan.serverflowable.entity.process;

import lombok.Data;

import java.util.List;

/**
 * @Auther rongchunjian
 * @Date 2021/3/25
 */

@Data
public class NodeVO {
    private Integer allowAddApprover;

    private Integer allowDelivery;

    private Integer allowRollback;

    private Integer approvalMethod;

    private boolean approverChosenMulti;

    private List<ApproverChosenRangeVO> approverChosenRange;

    private List<ApproverGroupVO> approverGroup;

    private String customId;

    private List<EmptyAssigneeVO> emptyAssigneeList;

    private boolean emptyAutoPass;

    private List<String> endCc;

    private String endCcType;

    private String id;

    private String name;

    private Integer nodeType;

    private PrivilegeVO privilege;

    private List<String> startCc;

    private Integer starterAssignee;

    /**
     * 0-或签；1-会签
     */
    private Integer multiInstance;

    private List<String> copyUserList;

}
