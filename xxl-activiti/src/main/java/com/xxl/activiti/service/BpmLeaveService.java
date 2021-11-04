package com.xxl.activiti.service;


import com.xxl.activiti.model.Result;
import com.xxl.activiti.model.entity.BpmEntity;
import com.xxl.activiti.model.entity.BpmLeave;

/**
 * @Description 请假流程
 * @Author xxl
 */
public interface BpmLeaveService {

    /**
     * 获取
     * @param id
     * @return
     */
    Result get(Long id);

    /**
     * 保存
     * @param leave
     * @return
     */
    Result save(BpmLeave leave);

    /**
     * 审核
     * @param entity
     * @return
     */
    Result audit(BpmEntity entity);

    /**
     * 我发起的休假
     * @param leave
     * @return
     */
    Result myList(BpmLeave leave);

    /**
     * 休假待办任务
     * @param leave
     * @return
     */
    Result toDoList(BpmLeave leave);

    /**
     * 休假已办任务
     * @param leave
     * @return
     */
    Result doneList(BpmLeave leave);

}