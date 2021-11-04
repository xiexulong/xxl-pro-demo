package com.xxl.securitydemo.security.service;

import com.xxl.securitydemo.dao.RoleDao;
import com.xxl.securitydemo.dao.UserDao;
import com.xxl.securitydemo.domain.po.SysUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * 自定义CA的 UserDetailsService 接口实现
 */
public class CertificateAuthorityJdbcUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    private RoleDao roleDao;

    @Override
    public UserDetails loadUserByUsername(String signature) throws UsernameNotFoundException {
        SysUser sysUser = this.userDao.getBySignature(signature);

        User.UserBuilder builder = User.builder()
                .username(sysUser.getUsername())
                .password(sysUser.getPassword());

        List<String> roles = this.roleDao.listRoleCodeByUserId(sysUser.getId());

        builder.roles(roles == null ? new String[] {} : roles.toArray(new String[] {}));

        return builder.build();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public RoleDao getRoleDao() {
        return roleDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}
