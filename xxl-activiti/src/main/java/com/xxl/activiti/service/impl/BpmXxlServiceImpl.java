package com.xxl.activiti.service.impl;

import cn.hutool.core.io.FileUtil;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BpmXxlServiceImpl {

    public static void main(String[] args) {

    }

    /**
     * 部署流程
     */
    public static void deployProcess() {

        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //创建流程部署对象
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
        //读取单个的流程定义文件
        deploymentBuilder.addClasspathResource("/process/test.bpmn");
        deploymentBuilder.addClasspathResource("/process/test.png");
        Deployment deployment = deploymentBuilder.deploy();//部署流程
    }

    public static void getDeploymentByName() {
        //查询一次部署对应的流程定义文件和对应的输入流
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String deploymentId = "901";
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<String> list = repositoryService.getDeploymentResourceNames(deploymentId);
        for (String name : list) {
            System.out.println(name);
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, name);
            //将文件保存到本地磁盘
            FileUtil.writeFromStream(inputStream, "");
        }

    }

    /**
     * 查询流程部署列表
     */
    public static void queryAllDeployment() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //部署查询对象，查询act_re_deployment 部署表
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        List<Deployment> list = deploymentQuery.list();
        for (Deployment deployment : list) {
            String id = deployment.getId();
            System.out.println(id);
        }
    }

    /**
     * 查询流程定义列表
     * activiti中的查询有两种方式：query API and native queries
     * 这里使用query api
     */
    public static void queryAllProcessDefinition() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //流程定义查询对象，查询表act_re_procdef
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //processDefinitionQuery.orderByProcessDefinitionVersion().asc(); //可以排序查询
        List<ProcessDefinition> list = processDefinitionQuery.list();
        for (ProcessDefinition pd : list) {
            System.out.println(pd.getName() + "———" + pd.getId());
        }

    }

    public static ManagementService managementService;
    /**
     * 查询流程定义列表
     * activiti中的查询有两种方式：query API and native queries
     * 这里使用native queries
     */
    public static void queryAllProcessDefinition2() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //流程定义查询对象，查询表act_re_procdef
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createNativeProcessDefinitionQuery()
                .sql("SELECT count(*) FROM " + managementService.getTableName(ProcessDefinition.class) + " T WHERE T.KEY = #{key}")
                .parameter("key", "gonzoTask")
                .list();
        //ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //List<ProcessDefinition> list = processDefinitionQuery.list();
        for (ProcessDefinition pd : list) {
            System.out.println(pd.getName() + "———" + pd.getId());
        }

        /*long count = taskService.createNativeTaskQuery()
                .sql("SELECT count(*) FROM " + managementService.getTableName(Task.class) + " T1, "
                        + managementService.getTableName(VariableInstanceEntity.class) + " V1 WHERE V1.TASK_ID_ = T1.ID_")
                .count();*/

    }

    public static void queryProcessDefinitionByParam() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //    流程定义查询对象，用于查询act_re_procdef
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        //添加过滤条件
        query.processDefinitionKey("myProcess_1");
        //query.orderByProcessDefinitionId().desc();
        //    添加排序条件
        query.orderByProcessDefinitionVersion().desc();
        //添加分页查询
        query.listPage(0, 10);
        List<ProcessDefinition> list = query.list();
        for (ProcessDefinition pd : list) {
            System.out.println(pd.getName() + "---" + pd.getId());
        }
    }

    /**
     * 挂起暂停一个流程定义，当挂起流程定义的时候如果在启动流程实例则会抛异常
     */
    public static void suspendProcessDefinition() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.suspendProcessDefinitionByKey("vacationRequest");

        RuntimeService runtimeService = processEngine.getRuntimeService();
        try {
            runtimeService.startProcessInstanceByKey("vacationRequest");
        } catch (ActivitiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除部署信息
     */
    public static void deleteByDeployId(String deploymentId) {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //流程部署id  在act_re_deployment表中
        //deleteDeployment有两个参数 第一个删除 部署的内容的id，第二个是否级联删除，默认为false
        processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
    }

    /**
     * 启动流程实例
     * 启动流程实例的两种方法: 根据流程定义的id启动
     * @param processDefinitionId 流程定义id可在act_re_procdef表中查询到
     */
    public static void startProcessInstanceById(String processDefinitionId) {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //String processDefinitionId = "myProcess_1:7:1004";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        System.out.println(processInstance.getId());
    }

    /**
     * 启动流程实例
     * 启动流程实例的两种方法: 根据流程定义的key启动（自动选择最新版本的流程定义启动流程实例）
     * @param processDefinitionKey 流程定义id可在act_re_procdef表中查询到
     */
    public static void startProcessInstanceByKey(String processDefinitionKey) {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //方式二：根据流程定义的key启动（自动选择最新版本的流程定义启动流程实例）
        //String processDefinitionKey = "myProcess_1";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", 200);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        System.out.println(processInstance.getId());
    }

    public static void deleteProcessInstance() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = "1601";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.deleteProcessInstance(processInstanceId, "不想要了");


    }

    /**
     * 查询个人任务列表
     */
    public static void getAssigneeTaskList() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //创建任务查询
        TaskQuery taskQuery = taskService.createTaskQuery();
        //添加查询条件 办理人为xxl
        String assignee = "xxl";
        taskQuery.taskAssignee(assignee);
        //taskQuery.taskCandidateOrAssigned(user);
        List<Task> list = taskQuery.list();//查询所有
        for (Task task : list) {
            System.out.println(task.getId() + "——" + task.getName());
        }
    }

    /**
     * 查询候选人任务列表
     * 当前的任务可以由多个人其中的某一个人办理， 可以在设计流程图时指定多个办理人。Candidate Users 候选用户
     */
    public static void getCandidateTaskList() {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //创建任务查询
        TaskQuery taskQuery = taskService.createTaskQuery();
        //添加查询条件 办理人为xxl
        String candidateUser = "xxl";
        taskQuery.taskCandidateUser(candidateUser);
        List<Task> list = taskQuery.list();//查询所有
        for (Task task : list) {
            System.out.println(task.getId() + "——" + task.getName());
        }
    }

    /**
     * 拾取公共任务（将公共任务变为个人任务）
     */
    public static void claim(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String taskId="1602";
        String userId="王五";
        TaskService taskService = processEngine.getTaskService();
        taskService.claim(taskId,userId);

        //退回任务（将个人任务重新变为公共任务）
        //taskService.setAssignee(taskId,null);
    }

    /**
     * //任务id可在act_ru_task表中查询到
     * @param taskId
     */
    public static void handlerTask(String taskId) {
        //使用默认配置文件创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        // String taskId = "602";
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", "xxl");
        taskService.complete(taskId, variables);//传入任务id办理业务
        //taskService.delegateTask(taskId, userId);// 将任务委派给另外一个人，
        //taskService.resolveTask(taskId);//委托人无法判断如何审批，向被委托人征询意见，但是最终决定权保留在委托人手上(效果类似于打回去补填信息)
        System.out.println("办理成功");


    }

    /*public static void handlerTask(String taskId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String executionId = "1401"; //流程实例id
        runtimeService.signalEventReceived();
    }*/

    /**
     * 使用RuntimeService的方法设置
     */
    public static void setVariables() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String executionId = "601";//流程实例Id
        String variableName = "key3";
        String value = "value3";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.setVariable(executionId, variableName, value);
        // runtimeService.setVariables(excutionId, new HashMap<>());
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        System.out.println(variables);
        Object variableValue1 = runtimeService.getVariable(executionId, variableName);
        System.out.println(variableValue1);

        TaskService taskService = processEngine.getTaskService();
        String taskId = "704";
        taskService.setVariables(taskId, new HashMap<>());
        Map<String, Object> variables2 = taskService.getVariables(taskId);
        System.out.println(variables2);
        Object variableValue2 = taskService.getVariable(taskId, variableName);
        System.out.println(variableValue2);


    }

    public static void getHistoryProcessInstance() {
        //查询历史流程实例  act_hi_procinst
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = processEngine.getHistoryService().createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> list1 = historicProcessInstanceQuery.list();
        for (HistoricProcessInstance hi : list1) {
            System.out.println(hi.getId());
        }

        //查询历史活动数据 act_hi_actinst
        HistoricActivityInstanceQuery historicActivityInstanceQuery = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        //按照流程实例排序
        historicActivityInstanceQuery.orderByProcessInstanceId().desc();
        historicActivityInstanceQuery.orderByHistoricActivityInstanceEndTime().asc();
        List<HistoricActivityInstance> list2 = historicActivityInstanceQuery.list();
        for (HistoricActivityInstance hi : list2) {
            System.out.println(hi.getActivityId() + "——" + hi.getActivityName() + "——" + hi.getActivityType());
        }

        //查询历史任务数据 act_hi_taskinst
        HistoricTaskInstanceQuery query = processEngine.getHistoryService().createHistoricTaskInstanceQuery();
        query.orderByProcessInstanceId().asc();
        query.orderByHistoricTaskInstanceEndTime().asc();
        List<HistoricTaskInstance> list = query.list();
        for (HistoricTaskInstance hi:list){
            System.out.println(hi.getAssignee()+"——"+hi.getName()+"——"+hi.getStartTime());
        }
    }
}
