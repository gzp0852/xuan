package com.glx.xuan.serverflowable;


import com.glx.xuan.serverflowable.service.AfterSignUserTaskCmd;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class ProcessDefinitionTests {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

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
                .addClasspathResource("bpmn/leave.bpmn20.xml")               // 添加流程部署文件
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

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId("bd19f7d9-033c-11ed-92a8-5811220911b4").singleResult();

        System.out.println("processDefinition.getId() = " + processDefinition.getId());
        System.out.println("processDefinition.getName() = " + processDefinition.getName());
        System.out.println("processDefinition.getDeploymentId() = " + processDefinition.getDeploymentId());
        System.out.println("processDefinition.getDescription() = " + processDefinition.getDescription());

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId("95ec60c5-0429-11ed-a00a-5811220911b4")
                .singleResult();
        System.out.println(processInstance.getName());
    }

    /**
     * 删除流程定义
     */
    @Test
    void testDeleteDeploy() {
        // 删除部署的流程，第一个参数是id，如果部署的流程启动了就不允许删除
//        repositoryService.deleteDeployment("82577ac9-fb4c-11ec-b937-5811220911b4");
        // 第二个参数是级联删除， 如果流程启动了， 相关的任务一并会被删除。
//        repositoryService.deleteDeployment("67bcbf5e-fb49-11ec-b51e-5811220911b4", true);
        runtimeService.deleteProcessInstance("95ec60c5-0429-11ed-a00a-5811220911b4", "就是要删");
    }

    /**
     * leave.bpmn20.xml 请假流程
     */
    @Test
    public void testFlow() {
        // 发起请假
        Map<String, Object> map = new HashMap<>();
        map.put("day", 2);
        map.put("studentUser", "小明");
        ProcessInstance studentLeave = runtimeService.startProcessInstanceByKey("StudentLeave", map);
        Task task = taskService.createTaskQuery().processInstanceId(studentLeave.getId()).singleResult();
        taskService.complete(task.getId());

        /*// 老师审批
        List<Task> teacherTaskList = taskService.createTaskQuery().taskCandidateGroup("teacher").list();
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("outcome", "通过");
        for (Task teacherTask : teacherTaskList) {
            taskService.complete(teacherTask.getId(), teacherMap);
        }

        // 校长审批
        List<Task> principalTaskList = taskService.createTaskQuery().taskCandidateGroup("principal").list();
        Map<String, Object> principalMap = new HashMap<>();
        principalMap.put("outcome", "通过");
        for (Task principalTask : principalTaskList) {
            taskService.complete(principalTask.getId(), principalMap);
        }

        // 查看历史
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(studentLeave.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityName());
        }*/
    }

    /**
     * leave.bpmn20.xml 请假流程
     */
    @Test
    public void testAdopt() {
        String groupName = "teacher";
        // 老师审批
        List<Task> teacherTaskList = taskService.createTaskQuery().taskCandidateGroup(groupName).list();
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("outcome", "通过");
        for (Task teacherTask : teacherTaskList) {
            taskService.complete(teacherTask.getId(), teacherMap);
        }

//        Task task = taskService.createTaskQuery().taskCandidateOrAssigned("小明").singleResult();
//        taskService.complete(task.getId());

    }

    /**
     * 查看历史数据
     */
    @Test
    public void testHisQuery() {
        String processInstanceId = "fec85c78-fb72-11ec-b35b-5811220911b4";
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

    /**
     * 查看当前人的任务
     */
    @Test
    public void testTaskQuery() {
        String Assigned = "%100%";
        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(Assigned).list();
        List<Task> list = taskService.createTaskQuery().taskAssigneeLike(Assigned).list();
        for (Task task : list) {
            System.out.println(task.getName()+"---"+ task.getAssignee()
                    +"---"+task.getTaskDefinitionKey()+"---"+task.isSuspended());
            System.out.println(task.getProcessVariables().get("isTransfer"));

            runtimeService.getVariable(task.getExecutionId(), "isTransfer");

            DynamicUserTaskBuilder taskBuilder = new DynamicUserTaskBuilder();
            taskBuilder.setName("asdasasasdsadsadsad");
            taskBuilder.setId("usertask-005");
            taskBuilder.setAssignee("熏悟空13");

            managementService.executeCommand(
                    new AfterSignUserTaskCmd("95ec60c5-0429-11ed-a00a-5811220911b4"
                            ,taskBuilder,"8a65857a-042a-11ed-a3c6-5811220911b4"));
        }
    }

    /**
     * 挂起流程
     */
    @Test
    public void testActivation() {
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId("StudentLeave:1:352763e1-fb4d-11ec-8ae0-5811220911b4")
                .singleResult();
        // 获取流程定义的状态
        boolean suspended = processDefinition.isSuspended();
        System.out.println("suspended = " + suspended);
        if(suspended){
            // 表示被挂起
            System.out.println("准备激活流程定义");
            repositoryService.activateProcessDefinitionById("StudentLeave:1:352763e1-fb4d-11ec-8ae0-5811220911b4",true,null);
        }else{
            // 表示激活状态
            System.out.println("准备挂起流程");
            repositoryService.suspendProcessDefinitionById("StudentLeave:1:352763e1-fb4d-11ec-8ae0-5811220911b4",true,null);
        }
    }

    /**
     * 挂起流程
     */
    @Test
    public void nextFlowNode() {
        String node = "";
        String taskId = "";
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



}
