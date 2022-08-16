package com.glx.xuan.serverflowable.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cmd.AbstractDynamicInjectionCmd;
import org.flowable.engine.impl.dynamic.BaseDynamicSubProcessInjectUtil;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Collections;
import java.util.List;

/**
 * 后加签
 */
@Slf4j
@AllArgsConstructor
public class AfterSignUserTaskCmd extends AbstractDynamicInjectionCmd implements Command<Void> {
  //流程实例id
  protected String processInstanceId;

  //后加签的节点信息
  private DynamicUserTaskBuilder signUserTaskBuilder;

  //当前流程节点的id
  private String taskId;

  @Override
  public Void execute(CommandContext commandContext) {
    //AbstractDynamicInjectionCmd提供的修改方法入口
    createDerivedProcessDefinitionForProcessInstance(commandContext, processInstanceId);
    return null;
  }

  @Override
  protected void updateBpmnProcess(CommandContext commandContext, Process process,
                                   BpmnModel bpmnModel, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity) {

    //找到当前节点的实体
    TaskService taskService = CommandContextUtil.getTaskService(commandContext);
    TaskEntity taskEntity = taskService.getTask(taskId);
    if (taskEntity == null) {
      throw new FlowableObjectNotFoundException("task:" + taskId + " not found");
    }
    //查找当前节点对应的执行执行实体（感觉兴趣的可以搜下流程执行的实例信息 表为ACT_RU_EXECUTION）
    ExecutionEntity currentExecutionEntity = CommandContextUtil.getExecutionEntityManager(commandContext).findById(taskEntity.getExecutionId());
    if (currentExecutionEntity == null) {
      throw new FlowableObjectNotFoundException("task:" + taskId + ",execution:" + taskEntity.getExecutionId() + " not found");
    }
    //定义的节点id
    String activityId = currentExecutionEntity.getActivityId();
    FlowElement flowElement = process.getFlowElement(activityId, true);
    if (!(flowElement instanceof Task)) {
      throw new FlowableException("task type error");
    }
    Activity activity = (Activity) flowElement;
    //由于是后加签所以需要获取当前流程节点流出的sequence
    SequenceFlow currentTaskOutSequenceFlow = activity.getOutgoingFlows().get(0);

    //定义新的节点
    UserTask addUserTask = new UserTask();

    //流程的id 可以在调用的时候传入 也可以根据flwoable提供的方法生成
    String id = signUserTaskBuilder.getId();
    if (id == null) {
      id = signUserTaskBuilder.nextTaskId(process.getFlowElementMap());
    }
    addUserTask.setId(id);
    addUserTask.setName(signUserTaskBuilder.getName());
    addUserTask.setAssignee(signUserTaskBuilder.getAssignee());


    process.addFlowElement(addUserTask);
    signUserTaskBuilder.setDynamicTaskId(id);

    //定义新的sequence 将节点的source改为新的节点  taget 改为当前节点的target
    String flowId = "sequenceFlow-" + CommandContextUtil.getProcessEngineConfiguration(commandContext).getIdGenerator().getNextId();
    SequenceFlow newSequenceFlow = this.createSequenceFlow(addUserTask.getId(), currentTaskOutSequenceFlow.getTargetFlowElement().getId());
    newSequenceFlow.setId(flowId);
    process.addFlowElement(newSequenceFlow);


    activity.setOutgoingFlows(Collections.singletonList(newSequenceFlow));
    //修改原有sequence 将原来的sequence的taget改为新的节点
    currentTaskOutSequenceFlow.setTargetFlowElement(addUserTask);
    currentTaskOutSequenceFlow.setTargetRef(addUserTask.getId());


    //clean清除原有的一些图的坐标信息
    bpmnModel.getLocationMap().clear();
    bpmnModel.getFlowLocationMap().clear();
    bpmnModel.getLabelLocationMap().clear();
    //当前流程的重新定义
    BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);

  }

  @Override
  protected void updateExecutions(CommandContext commandContext, ProcessDefinitionEntity processDefinitionEntity, ExecutionEntity executionEntity, List<ExecutionEntity> list) {

  }

  //这个方法也可以单独写在一个工具类里
  public SequenceFlow createSequenceFlow(String from, String to) {
    SequenceFlow flow = new SequenceFlow();
    flow.setSourceRef(from);
    flow.setTargetRef(to);
    return flow;
  }
}