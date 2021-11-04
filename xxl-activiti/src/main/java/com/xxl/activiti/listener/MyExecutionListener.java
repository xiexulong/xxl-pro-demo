package com.xxl.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class MyExecutionListener implements ExecutionListener {

    ////当监听事件发生时执行此方法
    @Override
    public void notify(DelegateExecution delegateExecution) {
        delegateExecution.getVariables();
    }
}
