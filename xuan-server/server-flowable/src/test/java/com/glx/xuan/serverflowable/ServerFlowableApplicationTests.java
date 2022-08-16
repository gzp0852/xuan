package com.glx.xuan.serverflowable;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class ServerFlowableApplicationTests {

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
                .addClasspathResource("bpmn/multi.bpmn20.xml")               // 添加流程部署文件
                .name("请求流程")                                        //  设置流程部署名称
                .deploy();                                              // 执行部署操作

        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());

    }

    /**
     * 查询流程定义
     */
    @Test
    void testDeployQuery() {
        /*List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            System.out.println(deployment.getId());
            System.out.println(deployment.getName());
            System.out.println(deployment.getDeploymentTime());
        }*/

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId("fac0e2b5-00ec-11ed-b323-5811220911b4").singleResult();
        System.out.println("processDefinition.getId() = " + processDefinition.getId());
        System.out.println("processDefinition.getName() = " + processDefinition.getName());
        System.out.println("processDefinition.getDeploymentId() = " + processDefinition.getDeploymentId());
        System.out.println("processDefinition.getDescription() = " + processDefinition.getDescription());
    }

    /**
     * 查看历史数据
     */
    @Test
    public void testHisQuery() {
        String processInstanceId = "e68888bd-00ed-11ed-aef1-5811220911b4";
        // 查看历史
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityName()+"---"+ activity.getActivityId() + " took "
                    + activity.getDurationInMillis() + " milliseconds");

        }
    }

    @Test
    public void testFlow() {
        // 发起请假
        /*Map<String, Object> map = new HashMap<>();
        List<String> persons = new ArrayList<>();
        persons.add("a,b,c");
        persons.add("d,e,f");
        persons.add("g,h,i");
        map.put("assList", persons);
        ProcessInstance studentLeave = runtimeService.startProcessInstanceByKey("multiPro3", map);*/
        ProcessInstance studentLeave = runtimeService.startProcessInstanceByKey("multiPro3");
        Task task = taskService.createTaskQuery().processInstanceId(studentLeave.getId()).singleResult();
        System.out.println(task.getProcessInstanceId()+"---"+task.getName()+"---"+task.getAssignee());

    }

    /**
     * 查看当前人的任务
     */
    @Test
    public void testTaskQuery() {
        List<Task> list = taskService.createTaskQuery().taskAssigneeLike("%贝吉塔%").list();
        for (Task task : list) {
            System.out.println(task.getName()+"---"+ task.getAssignee()
                    +"---"+task.getTaskDefinitionKey()+"---"+task.isSuspended());
        }
    }

    /**
     * 查看当前人的任务
     */
    @Test
    public void testTaskQuery1() {
        List<Task> list = taskService.createTaskQuery().taskAssigneeLike("%e%").list();
        for (Task task : list) {
            System.out.println("任务名称：" + task.getName());
            System.out.println("taskId： " + task.getId());
            System.out.println("assignee: " + task.getAssignee());
            System.out.println("processInstanceId" + task.getProcessInstanceId());
        }
    }



    /**
     * 查看当前人的任务
     */
    @Test
    public void testTaskQuery2() {
        Task task = taskService.createTaskQuery().processInstanceId("cb5623e8-00f9-11ed-a8ea-5811220911b4").singleResult();
        if (task.getAssignee().contains(",")) {
            String[] arr = task.getAssignee().split(",");
            String nowUser = arr[0];
            String nextUser = arr[1];
            taskService.setOwner(task.getId(), nowUser);
            taskService.setAssignee(task.getId(), nextUser);
            task.setAssignee(task.getAssignee().substring(nowUser.length()+1));

        } else {
            complete(task.getId());
        }
    }

    /**
     * 挂起流程
     */
    @Test
    public void nextFlowNode() {
        String node = "next";
        String taskId = "cb76070d-00f9-11ed-a8ea-5811220911b4";
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
                    List<String> candidate = new ArrayList<>();
                    candidate.add("x");
                    candidate.add("y");
                    candidate.add("Z");
                    ((UserTask) targetFlow).setCandidateUsers(candidate);

                }
                // 如果下个审批节点为结束节点
                if (targetFlow instanceof EndEvent) {
                    System.out.println("下一节点为结束节点：id=" + targetFlow.getId() + ",name=" + targetFlow.getName());
                }
            }


        }
    }

    public void nextFlowNode1(String node, String taskId) {
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
                    List<String> candidate = new ArrayList<>();
                    candidate.add("x");
                    candidate.add("y");
                    candidate.add("Z");
                    ((UserTask) targetFlow).setCandidateUsers(candidate);
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
     * 挂起流程
     */
    @Test
    public void complete(String taskId) {
        String node = "next";
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
     * 挂起流程
     */
    @Test
    public void complete() {
    }
}
