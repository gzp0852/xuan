package com.glx.xuan.serverflowable.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaozepeng
 * @since 2022/7/12
 */
@Data
@ApiModel(value="ApproverNodeRuleAddParam对象", description="新增审批节点对象")
public class ApproverNodeVo {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "流程规则id;")
    @NotNull
    private Integer approverRuleId;

    @ApiModelProperty(value = "xml中流程任务id")
    private String usertaskId;

    @ApiModelProperty(value = "节点名称")
    private String approverName;

    @ApiModelProperty(value = "审批节点描述")
    private String approverDesc;

    @ApiModelProperty(value = "(多情况拼接)1.用户组2.用户角色；3.岗位；4组织部门；5相对岗位；6多级主管；")
    private String approverType;

    @ApiModelProperty(value = "多种数据拼接")
    private List<String> approverTypeList;

    public List<String> getApproverTypeList() {
        approverTypeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approverType)) {
            String[] strArr = approverType.split(",");
            approverTypeList = Arrays.asList(strArr);
        }
        return approverTypeList;
    }


    @ApiModelProperty(value = "审批人-用户")
    private String approvalUser;
    @ApiModelProperty(value = "审批人-用户")
    private List<String> approvalUserList;

    public List<String> getApprovalUserList() {
        approvalUserList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approvalUser)) {
            String[] strArr = approvalUser.split(",");
            approvalUserList = Arrays.asList(strArr);
        }
        return approvalUserList;
    }


    @ApiModelProperty(value = "审批人-组")
    private String approvalUserGroup;
    @ApiModelProperty(value = "审批人-组")
    private List<String> approvalUserGroupList;
    public List<String> getApprovalUserGroupList() {
        approvalUserGroupList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approvalUserGroup)) {
            String[] strArr = approvalUserGroup.split(",");
            approvalUserGroupList = Arrays.asList(strArr);
        }
        return approvalUserGroupList;
    }


    @ApiModelProperty(value = "审批人-角色")
    private String approvalRole;
    @ApiModelProperty(value = "审批人-角色")
    private List<String> approvalRoleList;
    public List<String> getApprovalRoleList() {
        approvalRoleList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approvalRole)) {
            String[] strArr = approvalRole.split(",");
            approvalRoleList = Arrays.asList(strArr);
        }
        return approvalRoleList;
    }


    @ApiModelProperty(value = "审批人-部门")
    private String approvalDepartment;
    @ApiModelProperty(value = "审批人-部门")
    private List<String> approvalDepartmentList;
    public List<String> getApprovalDepartmentList() {
        approvalDepartmentList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approvalDepartment)) {
            String[] strArr = approvalDepartment.split(",");
            approvalDepartmentList = Arrays.asList(strArr);
        }
        return approvalDepartmentList;
    }


    @ApiModelProperty(value = "审批人-岗位")
    private String approvalPosition;
    @ApiModelProperty(value = "审批人-岗位")
    private List<String> approvalPositionList;
    public List<String> getApprovalPositionList() {
        approvalPositionList = new ArrayList<>();
        if (StringUtils.isNotEmpty(approvalPosition)) {
            String[] strArr = approvalPosition.split(",");
            approvalPositionList = Arrays.asList(strArr);
        }
        return approvalPositionList;
    }


    @ApiModelProperty(value = "0-否，1-是")
    private Integer approvalRelativeRole;


    @ApiModelProperty(value = "0-否，非0-最高节点")
    private Integer approvalMulti;



    @ApiModelProperty(value = "审批人为空选择类型;1.自动通过；2.指定审批人；3.自动转交管理员；0.不做处理")
    private Integer approverNuType;



    @ApiModelProperty(value = "审批人为空选择类型值;1:null；2:人员集；3:admin")
    private String approverNuTypeValue;

    @ApiModelProperty(value = "会签/或签;1-会签；2-或签")
    private Integer multipleType;

    @ApiModelProperty(value = "会签比例(百分号前面数值)")
    private Double multipleRatio;


}
