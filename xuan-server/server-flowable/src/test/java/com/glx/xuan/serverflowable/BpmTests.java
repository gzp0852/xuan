package com.glx.xuan.serverflowable;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glx.xuan.serverflowable.entity.ApproverNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaozepeng
 * @since 2022/7/15
 */
@SpringBootTest
@Slf4j
public class BpmTests {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Test
    public void dealLeaveProcess() throws Exception {
        // TODO 如果是转办逻辑的话，要去杨光checkRule()方法中修改逻辑
        // TODO 1.全局更新 余超表中离职人员冗余字段， 加已离职
        //      2.全局更新 关表中离职人员？

        String userId = "孙悟空";
        // 1. 判断离职用户所有正在运行审批单
        List<Task> taskList = taskService.createTaskQuery()
                //.taskTenantId("") TODO 考虑租户
                .taskAssigneeLike("%"+userId+"%").list();

        if (CollUtil.isNotEmpty(taskList)) {
            // 2. 循环处理审批单
            for (Task task : taskList) {
                log.info("===当前任务id" + task.getId() + "; ===当前任务名称" + task.getName());
                // 2.1. 获取审批节点usertask_id
                String usertaskId = task.getTaskDefinitionKey();
                // 2.1 获取是否为转办任务
                boolean isTransfer =  ObjectUtils.isNotEmpty(
                        runtimeService.getVariable(task.getExecutionId(), "isTransfer")) ?
                        (boolean) runtimeService.getVariable(task.getExecutionId(), "isTransfer") : false;
                // 2.1.2 根据deploymentId查询外层流程规则，
                String processInstanceId = task.getProcessInstanceId();
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                // 获取部署id
                String deploymentId = processInstance.getDeploymentId();
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("deployment_id", deploymentId);
//                ApprovalRuleInfoEntity ruleInfo = approvalRuleInfoService.getOne(queryWrapper);
//                Long id = ruleInfo.getId();
                // 获取节点规则
//                List<ApproverNodeVo> approverNodeVoList = nodeRuleService.queryApproverNode(Math.toIntExact(id), null, usertaskId);
                List<ApproverNodeVo> approverNodeVoList = new ArrayList<>();
                ApproverNodeVo approverNodeVo1 = new ApproverNodeVo();
                approverNodeVo1.setMultipleType(1);
                approverNodeVo1.setApproverNuType(2);
                approverNodeVo1.setApproverNuTypeValue("孙悟空,天津饭,比克");
                approverNodeVoList.add(approverNodeVo1);
                if (CollUtil.isNotEmpty(approverNodeVoList)) {
                    log.info("===获取当前任务节点规则");
                    // 同一规则同一usertaskId只会存在一条数据
                    ApproverNodeVo approverNodeVo = approverNodeVoList.get(0);

                    // 判断是否是转办任务
                    if (isTransfer) {
                        log.info("===当前任务是转办的任务");
                        // 判断人员是否只有一个，只有一个则调用判空逻辑;不止一个，则重新赋值ass
                        String assignee = task.getAssignee();
                        if (assignee.contains(",")) {
                            String[] userIds = assignee.split(",");
                            List<String> isTransferUserList = new ArrayList<>(Arrays.asList(userIds));
                            isTransferUserList.remove(userId);
                            taskService.setAssignee(task.getId(), CollUtil.join(isTransferUserList, ","));
                            return;
                        } else {
                            // 如果是转办任务（这里的转办是说设置的参数为true就是为空转办）
                            log.info("===任务只有一个人，调用空节点规则");
                            nodeNUllRule(userId, approverNodeVo, task);
                            return;
                        }
                    }


                    // 获取当前节点有哪些人员
                    List<Task> allTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                    List<String> userIds = new ArrayList<>();
                    if (CollUtil.isNotEmpty(allTaskList)) {
                        // 会签还是或签
                        Integer multipleType = approverNodeVo.getMultipleType();
                        // todo 目前只判断当前节点人的信息
                        userIds = allTaskList.stream().map(Task::getAssignee).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(userIds) && userIds.size()>1) {
                            log.info("===同一审批存在多人");
                            // TODO 判断是否会签，会签自动通过
                            if (multipleType == 1) {
                                log.info("===会签，自动通过");
                                autoComplete(task, userId+"因离职原因自动审批通过会签");
                            } else {// 或签不管
                                log.info("===或签，不管");
                            }
                        } else if (CollUtil.isNotEmpty(userIds) && userIds.size() == 1 && userIds.get(0).equals(userId)){
                            // 当前任务节点只有离职人员一个人
                            // 调用节点为空规则
                            log.info("===任务只有一个人，调用空节点规则");
                            nodeNUllRule(userId, approverNodeVo, task);
                        }
                    } else {
                        // 不存在没有任务的情况， 因为一开始就是根据离职人员查当前任务， 所以必会有一个任务， 此处说明原因， 防止忘记， 后续有修改在此处添加逻辑
                        log.error("不存在沒有任務的情況");
                    }
                } else {
                    throw new Exception("当前任务不可能没有规则；taskId= "+task.getId()+"taskName= "+task.getName());
                }
            }
        }
    }


    /**
     * 为空时走此接口逻辑
     * @param userId
     * @param approverNodeVo
     * @param task
     */
    public void nodeNUllRule(String userId, ApproverNodeVo approverNodeVo, Task task) {
        // 审批人为空选择类型;1.自动通过；2.指定审批人；3.自动转交管理员；0.不做处理
        Integer approverNuType = approverNodeVo.getApproverNuType();
        if (approverNuType.equals(1)) {
            // 调用自动通过逻辑
            log.info("===类型1，自动通过");
            autoComplete(task, userId+"因离职原因自动审批通过");
        } else if (approverNuType.equals(2)) {
            // 获取指定审批人
            String approverUserIds = approverNodeVo.getApproverNuTypeValue();
            if (StringUtils.isNotEmpty(approverUserIds)) {
                log.info("===类型2，有指定人" + approverUserIds.toString());
                String[] userIdArr = approverUserIds.split(",");
                // TODO 校验人员是否都离职, 掉接口
                if (userIdArr.length>1) { // 没有离职
                    // 转办逻辑(转办给没有离职的人员，逗号分隔
                    log.info("===类型2，有指定人，多人;===转办多人");
                    transferTask(task, userId, approverUserIds, true);
                } else if (userIdArr.length == 1) {
                    log.info("===类型2，有指定人，只有一个未离职;===转办单人");
                    transferTask(task, userId, approverUserIds, false);
                } else { // 全部离职
                    // 自动通过
                    log.info("===类型2，指定人全部离职;===自动通过");
                    autoComplete(task, task.getName() + "节点指定审批人也已离职");
                }
            } else {
                // 自动通过
                log.info("===类型2，指定人为空");
                autoComplete(task, task.getName() + "节点指定审批人没有人员");
            }
        } else if (approverNuType.equals(3)) {
            // TODO 根据接口获取管理员ids
            String approverUserIds = "admin1,admin2";
            // 转办逻辑(因为是多人转办，用逗号存储)
            log.info("===类型3，转办管理员");
            transferTask(task, userId, approverUserIds, true);
        }
    }


    /**
     * 自动通过
     *
     * @param task
     * @param message
     */
    public void autoComplete(Task task, String message) {
        log.info("===自动通过接口");
        taskService.addComment(task.getId(), task.getProcessInstanceId(), message);
        // TODO 从静态表中获取人员
        HashMap<String, Object> variables = new HashMap<>();
        List<String> userList= new ArrayList<String>();
        userList.add("卡卡罗特");
        userList.add("贝吉塔");
        userList.add("弗利萨");
        userList.add("吉连");
        //设置参数
        variables.put("assList",userList);
        taskService.complete(task.getId(), variables);

        // TODO 同步更新静态表记录数据(余超？)

        // TODO 杨光checkRule方法

    }

    /**
     * 转办
     *
     * @param task
     * @param curUserId
     * @param acceptUserId
     * @param isMulti 转办是否是多人（即逗号分隔）
     */
    public void transferTask(Task task, String curUserId, String acceptUserId, boolean isMulti) {
        log.info("===转办接口");
        taskService.setOwner(task.getId(), curUserId);
        taskService.setAssignee(task.getId(), acceptUserId );
        if (isMulti) {
            taskService.setVariable(task.getId(), "isTransfer", isMulti);
        }
        // TODO 同步数据到静态表（关？）
    }

}
