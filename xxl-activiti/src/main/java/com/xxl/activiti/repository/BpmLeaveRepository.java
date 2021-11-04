package com.xxl.activiti.repository;

import com.xxl.activiti.model.entity.BpmLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpmLeaveRepository extends JpaRepository<BpmLeave, Long> {

}