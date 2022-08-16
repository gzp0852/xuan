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
        List<Long> userList= new ArrayList<Long>();
        userList.add(11111L);
        userList.add(22222L);
        userList.add(33333L);
        userList.add(44444L);
        map.put("assigneeList", userList);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, map);
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

        Map result = new HashMap();
        taskList.forEach(task->{
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
        List<Long> userList= new ArrayList<Long>();
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
