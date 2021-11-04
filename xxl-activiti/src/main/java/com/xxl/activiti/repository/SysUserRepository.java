package com.xxl.activiti.repository;

import com.xxl.activiti.model.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import  org.springframework.stereotype.Repository;

/**
 * sys_user Repository
 * Created by xxl
 * Sun Oct 27 13:03:00 CST 2019
*/ 
@Repository 
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
}

