package com.glx.xuan.serverflowable;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @author gaozepeng
 * @since 2022/7/14
 */
@SpringBootTest
@Slf4j
public class ProcessTests {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Test
    void contextLoads() {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().list();
        for(ProcessDefinition processDefinition : processList){
            log.info("ProcessDefinition name = {},deploymentId = {}", processDefinition.getName(), processDefinition.getDeploymentId());
        }
    }

    /**
     * 部署流程
     */
    @Test
    void testDeploy() {
        Deployment deployment = repositoryService.createDeployment()    // 创建deployment对象
                .addClasspathResource("bpmn/jiandan.bpmn20.xml")               // 添加流程部署文件
                .name("简单流程")                                        //  设置流程部署名称
                .deploy();                                              // 执行部署操作

        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
//        deployment.getId() = bd19f7d9-033c-11ed-92a8-5811220911b4
//        deployment.getName() = 请求流程

    }

    /**
     * 发起流程
     */
    @Test
    public void testFlow() {
        // 发起请假
        Map<String, Object> map = new HashMap<>();
        /*List<String> persons = new ArrayList<>();
        persons.add("a,b,c");
        persons.add("d,e,f");
        persons.add("g,h,i");
        map.put("assList", persons);
        ProcessInstance studentLeave = runtimeService.startProcessInstanceByKey("multiPro3", map);*/
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process_cb41msyl");
        List<Long> userList= new ArrayList<Long>();
        userList.add(11111L);
//        userList.add(22222L);
//        userList.add(33333L);
//        userList.add(44444L);
        map.put("assigneeList", userList);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process_cb41msyl", map);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("任务名称：" + task.getName());
        System.out.println("taskId： " + task.getId());
        System.out.println("assignee: " + task.getAssignee());
        System.out.println("processInstanceId: " + task.getProcessInstanceId());


    }
    public String taskId = "df7d73b4-03e8-11ed-9cc1-5811220911b4";

    /**
     * complete
     */
    @Test
    public void complete() {
//        Map<String, Object> map = new HashMap<>();
//        List<String> persons = new ArrayList<>();
//        persons.add("aaa");
//        persons.add("bbb");
////        persons.add("ccc");
//        map.put("assList", persons);
//        taskService.complete(taskId, map);
        Task task = taskService.createTaskQuery().taskId("73fa40d3-0703-11ed-983f-5811220911b4").singleResult();

        checkNodeEnd(task);

        taskService.complete("73fa40d3-0703-11ed-983f-5811220911b4");
//        taskService.complete("0c33e07f-0429-11ed-bc96-5811220911b4");
//        taskService.complete("0c33e083-0429-11ed-bc96-5811220911b4");
//        taskService.complete("a4a1715b-0428-11ed-862e-5811220911b4");

    }

    @Test
    public void complete1() {
//        Task task = taskService.createTaskQuery().taskId("dcbb566c-0731-11ed-bbdc-5811220911b4").singleResult();
//        List<Long> userList= new ArrayList<Long>();
////        userList.add(11111L);
//        userList.add(22222L);
//        userList.add(33333L);
//        userList.add(44444L);
//        transferTask(task, 22222L, userList, true);
//        taskService.complete("73fa40d3-0703-11ed-983f-5811220911b4");

        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId("59aa06e7-0416-11ed-b185-5811220911b4")
//                .processUnfinished()
                .taskAssignee("贝吉塔")
                .list();
//        taskService.addComment()
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println(historicTaskInstance.getAssignee());
            System.out.println(historicTaskInstance.getEndTime());
            System.out.println(historicTaskInstance.getTaskDefinitionKey());
            System.out.println(historicTaskInstance.getProcessInstanceId());
        }

    }

    /**
     * 转办
     *
     * @param task
     * @param curUserId
     * @param acceptUserIdList
     * @param isMulti 转办是否是多人（即逗号分隔）
     */
    public void transferTask(Task task, Long curUserId, List<Long> acceptUserIdList, boolean isMulti) {
        log.info("===转办接口");
        /*taskService.setOwner(task.getId(), curUserId);
        taskService.setAssignee(task.getId(), acceptUserId );
        if (isMulti) {
            taskService.setVariable(task.getId(), NodeConstand.IS_TRANSFER, isMulti);
        }*/
      /*  for (Long userId : acceptUserIdList) {
            runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), task.getProcessInstanceId(),
                    Collections.singletonMap("assignee", userId));
        }*/
        runtimeService.deleteMultiInstanceExecution(task.getExecutionId(), false);
        // TODO 同步数据到静态表（关？）
    }

    /**
     * 校验下一节点是否为end
     */
    public boolean checkNodeEnd(Task task) {
        ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        for (SequenceFlow sequenceFlow : outFlows) {
            // 下一个审批节点
            FlowElement targetFlow = sequenceFlow.getTargetFlowElement();
            // 如果下个审批节点为结束节点
            if (targetFlow instanceof EndEvent) {
                return true;
            }
        }
        return false;
    }

    /**
     * 节点信息
     */
    @Test
    public void nextFlowNode() {
        String node = "next";
        taskId = "42d13d92-0341-11ed-9032-5811220911b4";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        for (SequenceFlow sequenceFlow : outFlows) {
            //当前审批节点
            if ("now".equals(node)) {
                FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
                System.out.println("当前节点: id=" + sourceFlowElement.getId() + ",name=" + sourceFlowElement.getName());
            } else if ("next".equals(node)) {
                // 下一个审批节点
                FlowElement targetFlow = sequenceFlow.getTargetFlowElement();
                if (targetFlow instanceof UserTask) {
                    System.out.println("下一节点: id=" + targetFlow.getId() + ",name=" + targetFlow.getName());

                }
                // 如果下个审批节点为结束节点
                if (targetFlow instanceof EndEvent) {
                    System.out.println("下一节点为结束节点：id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
            }
        }
    }

    /**
     * 删除任务
     */
    @Test
    public void deltask() {
        taskService.deleteTask("42d13d92-0341-11ed-9032-5811220911b4");
    }

    /**
     * 转办任务
     */
    @Test
    public void changetask() {
        taskId = "bf43d3ba-03e9-11ed-ba13-5811220911b4";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        HashMap<String, Object> variables = new HashMap<>();
        for (SequenceFlow sequenceFlow : outFlows) {
            FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
            System.out.println("当前节点: id=" + sourceFlowElement.getId() + ",name=" + sourceFlowElement.getName());
            if (sourceFlowElement instanceof UserTask) {
                String  inputDataItem;
                UserTask userTask = (UserTask) sourceFlowElement;
                //判断是不是会签节点
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
                if(multiInstanceLoopCharacteristics != null){
                    //会签节点
                    //获取设置参数名称
                    inputDataItem = multiInstanceLoopCharacteristics.getInputDataItem().replace("${","").replace("}","");
                    //获取前端设置的用户或者组
                    if(userTask.getCandidateGroups().size() >= 0){
                        List candidateGroups = userTask.getCandidateGroups();
                        //如果是用户组查询组里的用户
                        List<String> userList= new ArrayList<String>();
                        userList.add("卡卡罗特");
                        userList.add("贝吉塔");
                        userList.add("弗利萨");
                        userList.add("吉连");
                        //设置参数
                        variables.put(inputDataItem,userList);
                    }
                }else{
                    //获取设置参数名称
                    inputDataItem = userTask.getAssignee().replace("${","").replace("}","");
                    //非会签节点
                    if(userTask.getCandidateUsers().size() >= 0){
                        //设置参数
//                        variables.put(inputDataItem,userTask.getCandidateUsers());
                        variables.put(inputDataItem,"zmc");
                    }
                }
                //将variables设置进去
//                runtimeService.setVariables(task.getExecutionId(),variables);
//                taskService.setVariables(task.getId(), variables);
//                taskService.setVariablesLocal(task.getId(), variables);
//                taskService.setAssignee(task.getId(), variables);
            }
        }
    }
}
