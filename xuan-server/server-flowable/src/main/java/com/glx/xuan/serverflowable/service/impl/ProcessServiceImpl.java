package com.glx.xuan.serverflowable.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.glx.xuan.base.common.constant.ProcessConstant;
import com.glx.xuan.serverflowable.entity.process.LineVO;
import com.glx.xuan.serverflowable.entity.process.NodeVO;
import com.glx.xuan.serverflowable.entity.process.ProcessVO;
import com.glx.xuan.serverflowable.service.ProcessService;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaozepeng
 * @since 2022/7/25
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Override
    public Map deployByXML(String xmlName) {
        Deployment deployment =
                // 创建deployment对象
                repositoryService.createDeployment()
                        // 添加流程部署文件
                        .addClasspathResource(ProcessConstant.XML_PATH + xmlName)
                        // 设置流程部署名称
                        .name("简单流程")
                        // 执行部署操作
                        .deploy();

        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
        Map result = new HashMap();
        result.put("deploymentId = ", deployment.getId());
        result.put("deploymentName = ", deployment.getName());
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deployment.getId())
                        .latestVersion()
                        .singleResult();
        result.put("processKey = ", processDefinition.getKey());
        return result;
    }

    /**
     * JSON转流程
     *
     * @param processVO
     * @return
     */
    public Map deployByVO(ProcessVO processVO) {
        // flowable部署流程定义
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        List<LineVO> lineList = processVO.getLineList();
        List<NodeVO> nodeList = processVO.getNodeList();
        Process process = new Process();
        // 处理节点
        process = dealNodes(process, nodeList);
        // 处理线段
        process = dealLines(process, lineList);
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.addProcess(process);
        // bpmn与xml转换
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        deploymentBuilder.addBytes(processVO.getProcessName() + ProcessConstant.BPMN_SUFFIX, convertToXML);
        // 部署流程定义
        Deployment deployment = deploymentBuilder.deploy();


        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
        Map result = new HashMap();
        result.put("deploymentId = ", deployment.getId());
        result.put("deploymentName = ", deployment.getName());
        ProcessDefinition processDefinition =
                repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deployment.getId())
                        .latestVersion()
                        .singleResult();
        result.put("processKey = ", processDefinition.getKey());
        return result;

    }

    private Process dealNodes(Process process, List<NodeVO> nodeList) {
        if (CollectionUtils.isNotEmpty(nodeList)) {
            for (NodeVO nodeVO : nodeList) {
                String id = nodeVO.getId();
                // 判断是否是起始节点
                if (id.startsWith("START")) {
                    StartEvent startEvent = new StartEvent();
                    startEvent.setId(nodeVO.getId());
                    startEvent.setName(nodeVO.getName());
                    startEvent.setFormKey("START");
                    // 设置启动流程表单属性（一期暂时没有改功能）
                    /*FieldVO field = nodeVO.getPrivilege().getField();
                    if (field != null) {
                        List<String> readable = field.getReadable();
                        List<String> writable = field.getWritable();
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(readable)) {
                            List<FormProperty> formProperties = new ArrayList<>();
                            for (String s : readable) {
                                FormProperty formProperty = new FormProperty();
                                formProperty.setReadable(true);
                                if (writable.contains(s)) {
                                    formProperty.setWriteable(true);
                                } else {
                                    formProperty.setWriteable(false);
                                }
                                formProperty.setId(s);
                                formProperty.setType("string");
                                formProperties.add(formProperty);
                            }
                            startEvent.setFormProperties(formProperties);
                        }
                    }*/
                    process.addFlowElement(startEvent);
                } else if (id.startsWith("ROUTE")) {
                    // 判断是否是网关
                    ExclusiveGateway exclusiveGateWay = new ExclusiveGateway();
                    exclusiveGateWay.setId(nodeVO.getId());
                    process.addFlowElement(exclusiveGateWay);
                } else if (id.startsWith("APPROVAL")) {
                    // 判断是否是审批节点
                    UserTask userTask = new UserTask();
                    userTask.setId(nodeVO.getId());
                    userTask.setName(nodeVO.getName());
                    // 设置节点表单属性（节点规则表单独存）
                    /*userTask.setFormKey(nodeVO.getId());
                    List<FormProperty> formProperties = new ArrayList<>();
                    FieldVO field = nodeVO.getPrivilege().getField();
                    if (field != null) {
                        List<String> readable = field.getReadable();
                        List<String> writable = field.getWritable();
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(readable)) {
                            for (String s : readable) {
                                FormProperty formProperty = new FormProperty();
                                formProperty.setReadable(true);
                                if (writable.contains(s)) {
                                    formProperty.setWriteable(true);
                                } else {
                                    formProperty.setWriteable(false);
                                }
                                formProperty.setId(s);
                                formProperty.setType("string");
                                formProperties.add(formProperty);
                            }
                        }
                    }*/
                    /*
                     *设置审批人（节点规则表单独存），涉及到是否会签/或签节点和人员自动通过规则，故把每个节点设置成并行会签节点，根据会签/或签设置完成条件
                        nrOfInstances：实例总数。
                        nrOfActiveInstances：当前活动的（即未完成的），实例数量。对于顺序多实例，这个值总为1。
                        nrOfCompletedInstances：已完成的实例数量。
                        loopCounter：给定实例在for-each循环中的index。
                     */
                    MultiInstanceLoopCharacteristics mu = new MultiInstanceLoopCharacteristics();
                    // 默认并行会签
                    mu.setSequential(false);
                    // 判断是否或签
                    if (NumberUtil.equals(0, nodeVO.getMultiInstance())) {
                        // 完成条件
                        mu.setCompletionCondition("${nrOfCompletedInstances > completionCount}");
                    } else {
                        // 完成条件
                        mu.setCompletionCondition("${nrOfCompletedInstances == completionCount}");
                    }
                    // 审批人集合参数
                    mu.setInputDataItem(ProcessConstant.ASSIGNEE_LIST);
                    // 迭代集合
                    mu.setElementVariable(ProcessConstant.ASSIGNEE);
                    userTask.setLoopCharacteristics(mu);
                    userTask.setAssignee(ProcessConstant.VAR_ASSIGNEE);

                    /*List<ApproverGroupVO> approverGroup = nodeVO.getApproverGroup();
                    List<String> candidateGroups = new ArrayList<>();
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(approverGroup)) {
                        for (ApproverGroupVO approverGroupVO : approverGroup) {
                            Integer approverType = approverGroupVO.getApproverType();
                            FormProperty formProperty = new FormProperty();
                            formProperty.setId("currentOperator");
                            formProperty.setType("string");
                            formProperties.add(formProperty);
                            userTask.setFormProperties(formProperties);
                            if (approverType.intValue() == 1) {
                                //角色
                                //${auditorpojo.role}
                                userTask.setCandidateGroups(approverGroupVO.getApproverIds());
                            } else if (approverType.intValue() == 5) {
                                //部门领导人
                                List<String> deptLeaders = new ArrayList<>();
                                String s = approverGroupVO.getApproverIds().get(0);
                                if ("1".equals(s)) {
                                    deptLeaders.add("${auditorpojo.deptLeader}");
                                } else if ("2".equals(s)) {
                                    deptLeaders.add("${auditorpojo.upDeptLeader}");
                                } else if ("3".equals(s)) {
                                    deptLeaders.add("${auditorpojo.upUpDeptLeader}");
                                } else if ("4".equals(s)) {
                                    deptLeaders.add("${auditorpojo.upUpUpDeptLeader}");
                                } else if ("5".equals(s)) {
                                    deptLeaders.add("${auditorpojo.upUpUpUpDeptLeader}");
                                }
                                userTask.setCandidateUsers(deptLeaders);
                            } else if (approverType.intValue() == 0) {
                                //上级
                                List<String> leaders = new ArrayList<>();
                                String s = approverGroupVO.getApproverIds().get(0);
                                if ("1".equals(s)) {
                                    leaders.add("${auditorpojo.leader}");
                                } else if ("2".equals(s)) {
                                    leaders.add("${auditorpojo.upLeader}");
                                } else if ("3".equals(s)) {
                                    leaders.add("${auditorpojo.upUpLeader}");
                                } else if ("4".equals(s)) {
                                    leaders.add("${auditorpojo.upUpUpLeader}");
                                } else if ("5".equals(s)) {
                                    leaders.add("${auditorpojo.upUpUpUpLeader}");
                                }
                                userTask.setCandidateUsers(leaders);
                            } else if (approverType.intValue() == 2) {
                                //指定人员
                                userTask.setCandidateUsers(approverGroupVO.getApproverIds());
                            } else if (approverType.intValue() == 8) {
                                //发起者本人
                                List<String> selfUsers = new ArrayList<>();
                                selfUsers.add("${auditorpojo.selfUserId}");
                                userTask.setCandidateUsers(selfUsers);
                            } else if (approverType.intValue() == 11) {
                                String s = approverGroupVO.getApproverIds().get(0);
                                if ("1".equals(s)) {
                                    MultiInstanceLoopCharacteristics mu = new MultiInstanceLoopCharacteristics();
                                    mu.setSequential(true);
                                    mu.setCompletionCondition("${userService.getDirectLeader(currentOperator)==null}");
                                    mu.setLoopCardinality("10");
                                    userTask.setLoopCharacteristics(mu);
                                    userTask.setAssignee("${userService.getDirectLeader(currentOperator)}");
                                }
                            }
                        }
                        userTask.setFormProperties(formProperties);
                        userTask.setCandidateGroups(candidateGroups);
                    }*/
                    process.addFlowElement(userTask);
                } else if (id.startsWith("END")) {
                    EndEvent endEvent = new EndEvent();
                    endEvent.setId(nodeVO.getId());
                    endEvent.setName(nodeVO.getName());
                    process.addFlowElement(endEvent);
                }
            }
        }
        return process;
    }

    private Process dealLines(Process process, List<LineVO> lineList) {
        if (CollectionUtil.isNotEmpty(lineList)) {
            // 根据优先级排序
            lineList =
                    lineList.stream().sorted(Comparator.comparingInt(LineVO::getPriority)).collect(Collectors.toList());
            for (LineVO lineVO : lineList) {
                SequenceFlow sequenceFlow = new SequenceFlow();
                // 连线id
                sequenceFlow.setId(lineVO.getId());
                // 上一节点id
                sequenceFlow.setSourceRef(lineVO.getSrcId());
                // 下一节点id
                sequenceFlow.setTargetRef(lineVO.getDstId());
                // 判断是否有条件跳转（一期暂时不做）
                /*List<List<ConditionGroupListVO>> conditionGroupList = lineVO.getConditionGroupList();
                  if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(conditionGroupList)) {
                    StringBuilder conditionSb = new StringBuilder();
                    for (List<ConditionGroupListVO> conditionGroupListVOS : conditionGroupList) {
                        for (int i = 0; i < conditionGroupListVOS.size(); i++) {
                            ConditionGroupListVO conditionGroupListVO = conditionGroupListVOS.get(i);
                            String formId = conditionGroupListVO.getFormId();
                            String operationCode = conditionGroupListVO.getOperationCode();
                            String type = conditionGroupListVO.getType();
                            if (conditionSb.length() > 0) {
                                conditionSb.append(" and ");
                            } else {
                                conditionSb.append("${");
                            }
                            Object value = conditionGroupListVO.getValue();
                            switch (operationCode) {
                                case "1":
                                    operationCode = "<";
                                    break;
                                case "2":
                                    operationCode = "<=";
                                    break;
                                case "3":
                                    operationCode = "==";
                                    break;
                                case "4":
                                    operationCode = ">";
                                    break;
                                case "5":
                                    operationCode = ">=";
                                    break;
                                default:
                                    operationCode = "==";
                                    break;
                            }
                            if (type.equals("applicant")) {
                                List<Map<String, Object>> value1 = (List<Map<String, Object>>) value;
                                for (Map<String, Object> map : value1) {
                                    List<String> approverIds = (List<String>) map.get("approverIds");
                                    for (int i1 = 0; i1 < approverIds.size(); i1++) {
                                        String s = approverIds.get(i1);
                                        if (i1 != approverIds.size() - 1) {
                                            conditionSb.append(formId).append(operationCode).append(s).append(" or ");
                                        } else {
                                            conditionSb.append(formId).append(operationCode).append(s);
                                        }
                                    }
                                }
                            } else {
                                conditionSb.append(formId).append(operationCode).append(value);
                            }

                        }
                    }
                    conditionSb.append("}");
                    sequenceFlow.setConditionExpression(conditionSb.toString());
                }*/
                process.addFlowElement(sequenceFlow);
            }
        }
        return process;
    }

    @Override
    public Map startProcess(String processKey) {
        // 发起请假
        Map<String, Object> map = new HashMap<>();
        List<Long> userList = new ArrayList<Long>();
        userList.add(11111L);
        userList.add(22222L);
        userList.add(33333L);
        userList.add(44444L);
        map.put("assigneeList", userList);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, map);
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

        Map result = new HashMap();
        taskList.forEach(task -> {
            System.out.println("任务名称：" + task.getName());
            System.out.println("taskId： " + task.getId());
            System.out.println("assignee: " + task.getAssignee());
            System.out.println("processInstanceId: " + task.getProcessInstanceId());
            result.put("taskName = ", task.getName());
            result.put("taskId = ", task.getId());
            result.put("assignee = ", task.getAssignee());
            result.put("processInstanceId = ", task.getProcessInstanceId());
        });
        return result;
    }


    public void complete(String taskId, String appId) {
        Map<String, Object> map = new HashMap<>();
        List<Long> userList = new ArrayList<Long>();
        userList.add(11111L);
        userList.add(22222L);
        userList.add(33333L);
        userList.add(44444L);
        map.put("assigneeList", userList);

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "完成任务");
        taskService.complete(taskId, map);

    }
}

/*

    public CommonResult backToStep(BackVo backVo) {
        CommonResult returnVo = CommonResult.success(ResultCode.SUCCESS);
        if (backVo != null && StringUtils.isNotBlank(backVo.getTaskId())) {
            TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(backVo.getTaskId()).singleResult();
            if (taskEntity != null) {
                Activity distActivity = processDefinitionUtils.findFlowElementById(taskEntity.getProcessDefinitionId(), backVo.getDistFlowElementId());
                if (taskEntity != null && distActivity != null) {
                    //1. 判断该节点上一个节点是不是并行网关节点
                    List<SequenceFlow> incomingFlows = distActivity.getIncomingFlows();
                    if (CollectionUtils.isNotEmpty(incomingFlows)) {
                        for (SequenceFlow sequenceFlow : incomingFlows) {
                            FlowElement upNode = sequenceFlow.getSourceFlowElement();
                            if (upNode != null && (upNode instanceof ParallelGateway || upNode instanceof InclusiveGateway)) {
                                returnVo = new ReturnVo<>(ReturnCode.FAIL, "并行节点无法驳回，请选择其他节点!");
                                return returnVo;
                            }
                        }
                    }
                    //2. 如果上一个节点是提交者的话要处理一下
                    if (FlowConstant.FLOW_SUBMITTER.equals(distActivity.getName())) {
                        //查找发起人
                        ExtendHisprocinst extendHisprocinst = this.extendHisprocinstService.findExtendHisprocinstByProcessInstanceId(taskEntity.getProcessInstanceId());
                        if (extendHisprocinst != null) {
                            runtimeService.setVariable(taskEntity.getProcessInstanceId(), FlowConstant.FLOW_SUBMITTER, extendHisprocinst.getCreator());
                        }
                    }
                    //3. 添加审批意见和修改流程状态
                    this.addCommentAndUpdateProcessStatus(backVo);
                    //4. 删除现有的所有任务
                    managementService.executeCommand(new DeleteTaskCmd(taskEntity.getProcessInstanceId()));
                    //5. 删除节点信息
                    this.deleteHisActivities(distActivity, taskEntity.getProcessInstanceId());
                    //6. 驳回到disk节点
                    Activity currActivity = processDefinitionUtils.findFlowElementById(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey());
                    //6.1 如果当前节点是多实例节点 删除当前多实例 如果目标节点不是多实例我们就创建一个孩子实例
                    boolean flag = false;
                    if (currActivity.getBehavior() instanceof MultiInstanceActivityBehavior) {
                        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId()).singleResult();
                        managementService.executeCommand(new DeleteMultiInstanceExecutionCmd(executionEntity.getParentId(), false));
                        flag = true;
                    }
                    //6.2 处理并行网关的多实例
                    List<Execution> executions = runtimeService.createExecutionQuery().parentId(taskEntity.getProcessInstanceId()).list();
                    if (CollectionUtils.isNotEmpty(executions) && executions.size() > 1) {
                        executions.forEach(execution -> {
                            ExecutionEntity e = (ExecutionEntity) execution;
                            managementService.executeCommand(new DeleteChildExecutionCmd(e));
                        });
                        flag = true;
                    }
                    if (flag) {
                        ExecutionEntity parentExecutionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(taskEntity.getProcessInstanceId()).singleResult();
                        managementService.executeCommand(new AddChildExecutionCmd(parentExecutionEntity));
                    }
                    managementService.executeCommand(new JumpActivityCmd(taskEntity.getProcessInstanceId(), distActivity.getId()));
                    //TODO 7. 处理加签的数据0
                }
            } else {
                returnVo = new ReturnVo<>(ReturnCode.FAIL, "当前任务不存在!");
            }
        } else {
            returnVo = new ReturnVo<>(ReturnCode.FAIL, "请设置相关参数!");
        }
        return returnVo;
    }

    */
/**
     * 添加审批意见和修改流程状态
     *
     * @param baseProcessVo 基本流程任务参数
     *//*

    protected void addCommentAndUpdateProcessStatus(BaseProcessVo baseProcessVo) {
        //兼容处理
        if (StringUtils.isBlank(baseProcessVo.getProcessInstanceId())) {
            Task task = taskService.createTaskQuery().taskId(baseProcessVo.getTaskId()).singleResult();
            if (task != null) {
                baseProcessVo.setProcessInstanceId(task.getProcessInstanceId());
            }
        }
        //1.添加审批意见
        FlowCommentVo flowCommentVo = new FlowCommentVo(baseProcessVo.getTaskId(), baseProcessVo.getUserCode(),
                baseProcessVo.getProcessInstanceId(), baseProcessVo.getMessage(), baseProcessVo.getCommentTypeEnum().toString());
        this.addFlowComment(flowCommentVo);
        //2.修改流程实例的状态
        ExtendHisprocinst extendHisprocinst = new ExtendHisprocinst(baseProcessVo.getProcessInstanceId(), baseProcessVo.getProcessStatusEnum().toString());
        extendHisprocinstService.updateStatusByProcessInstanceId(extendHisprocinst);
        //3.TODO 生成索引
    }

    */
/**
     * 添加审批意见
     *
     * @param flowCommentVo
     *//*

    private void addFlowComment(FlowCommentVo flowCommentVo) {
        FlowCommentCmd cmd = new FlowCommentCmd(flowCommentVo.getTaskId(), flowCommentVo.getUserId(),
                flowCommentVo.getProcessInstanceId(), flowCommentVo.getType(), flowCommentVo.getMessage());
        managementService.executeCommand(cmd);
    }


}

// DeleteTaskCmd 删除任务命令

public class DeleteTaskCmd implements Command<Void> {

    private String processInstanceId;

    public DeleteTaskCmd(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntityManager taskEntityManager = CommandContextUtil.getTaskEntityManager(commandContext);
        ExecutionEntityManager executionEntityManager = org.flowable.engine.impl.util.CommandContextUtil.getExecutionEntityManager(commandContext);
        List<ExecutionEntity> executionEntities = executionEntityManager.findChildExecutionsByProcessInstanceId(processInstanceId);
        executionEntities.forEach(executionEntity -> taskEntityManager.deleteTasksByExecutionId(executionEntity.getId()));
        return null;
    }
}


// AddChildExecutionCmd 添加一个流程实例下面的执行实例

class AddChildExecutionCmd implements Command<Void> {

    private ExecutionEntity parentExecutionEntity;

    public AddChildExecutionCmd(ExecutionEntity parentExecutionEntity) {
        this.parentExecutionEntity = parentExecutionEntity;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        executionEntityManager.createChildExecution(parentExecutionEntity);
        return null;
    }
}

//   DeleteChildExecutionCmd 删除执行实例

class DeleteChildExecutionCmd implements Command<Void> {

    private ExecutionEntity child;

    public DeleteChildExecutionCmd(ExecutionEntity child) {
        this.child = child;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        executionEntityManager.delete(child, true);
        return null;
    }
}

//  JumpActivityCmd 执行跳转

class JumpActivityCmd implements Command<Void> {

    private String target;
    private String processInstanceId;

    public JumpActivityCmd(String processInstanceId, String target) {
        this.processInstanceId = processInstanceId;
        this.target = target;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        List<ExecutionEntity> executionEntities = executionEntityManager.findChildExecutionsByParentExecutionId(processInstanceId);
        Process process = ProcessDefinitionUtil.getProcess(executionEntities.get(0).getProcessDefinitionId());
        FlowNode targetFlowElement = (FlowNode) process.getFlowElement(target);
        FlowableEngineAgenda agenda = CommandContextUtil.getAgenda();
        executionEntities.forEach(execution -> {
            execution.setCurrentFlowElement(targetFlowElement);
            agenda.planContinueProcessInCompensation(execution);
        });
        return null;
    }
}
*/
