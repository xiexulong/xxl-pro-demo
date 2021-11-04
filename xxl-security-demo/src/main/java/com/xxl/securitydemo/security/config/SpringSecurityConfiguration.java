package com.xxl.securitydemo.security.config;

import com.xxl.securitydemo.dao.FuncDao;
import com.xxl.securitydemo.dao.RoleDao;
import com.xxl.securitydemo.dao.UserDao;
import com.xxl.securitydemo.domain.po.SysFuncRole;
import com.xxl.securitydemo.security.ca.CertificateAuthorityAuthenticationFilter;
import com.xxl.securitydemo.security.ca.CertificateAuthorityDaoAuthenticationProvider;
import com.xxl.securitydemo.security.filter.UsernamePasswordCaptchaAuthenticationFilter;
import com.xxl.securitydemo.security.handler.CustomLogoutSuccessHandler;
import com.xxl.securitydemo.security.handler.CustomSavedRequestAwareAuthenticationSuccessHandler;
import com.xxl.securitydemo.security.handler.CustomSimpleUrlAuthenticationFailureHandler;
import com.xxl.securitydemo.security.intercept.CustomFilterSecurityInterceptor;
import com.xxl.securitydemo.security.service.CertificateAuthorityJdbcUserDetailsService;
import com.xxl.securitydemo.security.service.CustomJdbcUserDetailsService;
import com.xxl.securitydemo.service.EmailService;
import com.xxl.securitydemo.service.SmsService;
import com.xxl.securitydemo.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static String DEFAULT_REMEMBER_ME_KEY = "default remember me key";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private FuncDao funcDao;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private WeChatService wechatService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/plugins/**", "/images/**", "/fonts/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {



        httpSecurity
                .formLogin()
                    //自定义用户名密码参数名及用户名密码验证路径
                    //.loginProcessingUrl("/j_spring_security_check")
                    //.usernameParameter("j_username")
                    //.passwordParameter("j_password")
                    .loginPage("/login")
                    .successHandler(customAuthenticationSuccessHandler())// 这些处理器的处理逻辑也可以在过滤器中实现
                    //.defaultSuccessUrl("/index") //可在customAuthenticationSuccessHandler中配置
                    .failureHandler(customSimpleUrlAuthenticationFailureHandler())
                    //.failureUrl("/login_fail")//可在customSimpleUrlAuthenticationFailureHandler中配置
                    .permitAll().and()
                .logout()
                    .logoutSuccessHandler(customLogoutSuccessHandler())
                //.logoutSuccessUrl("/logout_success") //可在customLogoutSuccessHandler中配置
                .permitAll().and()
                .rememberMe()
                .key(DEFAULT_REMEMBER_ME_KEY)
                .rememberMeServices(tokenBasedRememberMeServices())
                .userDetailsService(userDetailsService())
                .tokenValiditySeconds(14 * 24 * 60 * 60).and()
                .authorizeRequests()
                .antMatchers("/logout_success").permitAll()
                .antMatchers("/captcha/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable()
        ;


        // ca 登陆的过滤器
        httpSecurity.addFilterBefore(certificateAuthorityAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //使用账户密码验证码的方式的过滤器来覆盖默认的账户密码登陆
        httpSecurity.addFilterAt(usernamePasswordCaptchaAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //资源权限动态控制的拦截器
        httpSecurity.addFilterAfter(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }







    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .authenticationProvider(certificateAuthorityDaoAuthenticationProvider())
            //配置 UserDetailsService 为我们刚才创建的自定义实现。另外，密码加密方式使用的是 BCryptPasswordEncoder。
            .userDetailsService(customJdbcUserDetailsService())
            .passwordEncoder(new BCryptPasswordEncoder());
    }

    private RememberMeServices tokenBasedRememberMeServices() {
        return new TokenBasedRememberMeServices(DEFAULT_REMEMBER_ME_KEY, userDetailsService());
    }

    /***********  验证码相关 start ************/
    private UsernamePasswordCaptchaAuthenticationFilter usernamePasswordCaptchaAuthenticationFilter() throws Exception {
        UsernamePasswordCaptchaAuthenticationFilter authenticationFilter = new UsernamePasswordCaptchaAuthenticationFilter();
        authenticationFilter.setRememberMeServices(tokenBasedRememberMeServices());
        authenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(customSimpleUrlAuthenticationFailureHandler());
        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }
    /***********  验证码相关 end ************/
    /**
     * ca过滤器处理成功登陆和失败登陆
     * @return
     * @throws Exception
     */
    private AbstractAuthenticationProcessingFilter certificateAuthorityAuthenticationFilter() throws Exception {
        CertificateAuthorityAuthenticationFilter authorityAuthenticationFilter = new CertificateAuthorityAuthenticationFilter();
        authorityAuthenticationFilter.setAuthenticationSuccessHandler(certificateAuthorityAuthenticationSuccessHandler());
        authorityAuthenticationFilter.setAuthenticationFailureHandler(certificateAuthorityAuthenticationFailureHandler());
        authorityAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return authorityAuthenticationFilter;
    }

    private AuthenticationProvider certificateAuthorityDaoAuthenticationProvider() {
        CertificateAuthorityDaoAuthenticationProvider certificateAuthorityDaoAuthenticationProvider = new CertificateAuthorityDaoAuthenticationProvider();
        certificateAuthorityDaoAuthenticationProvider.setUserDetailsService(certificateAuthorityJdbcUserDetailsService());
        return certificateAuthorityDaoAuthenticationProvider;
    }

    /**
     * 登陆成功自定义处理逻辑
     */
    private AuthenticationSuccessHandler certificateAuthorityAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setDefaultTargetUrl("/index");
        return authenticationSuccessHandler;
    }
    /**
     * 登陆失败自定义处理逻辑
     */
    private AuthenticationFailureHandler certificateAuthorityAuthenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
        authenticationFailureHandler.setDefaultFailureUrl("/login_fail");
        return authenticationFailureHandler;
    }

    /**
     * 自定义 CA 的 UserDetailsService类通过签名从数据库中获取用户信息
     * @return
     */
    private UserDetailsService certificateAuthorityJdbcUserDetailsService() {
        CertificateAuthorityJdbcUserDetailsService userDetailsService = new CertificateAuthorityJdbcUserDetailsService();
        userDetailsService.setUserDao(userDao);
        userDetailsService.setRoleDao(roleDao);
        return userDetailsService;
    }

    /**
     * 资源权限动态控制的拦截器
     */
    private FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        CustomFilterSecurityInterceptor filterSecurityInterceptor = new CustomFilterSecurityInterceptor();
        //过滤器调用安全元数据源
        filterSecurityInterceptor.setSecurityMetadataSource(new DefaultFilterInvocationSecurityMetadataSource(obtainRequestMap()));
        //访问决策管理器
        filterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
        //认证管理器
        filterSecurityInterceptor.setAuthenticationManager(authenticationManager());
        return filterSecurityInterceptor;
    }
    /**
     * 实现访问决策器
     * 一旦给用户分配了某个功能，即代表该功能下的所有操作用户都可以访问。
     * 另外，如果一个功能有多个角色控制，那么我们默认只要分配了其中的任何一个角色，都可以访问该功能
     */
    private AccessDecisionManager accessDecisionManager() {
        //访问决策投票器
        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
        voters.add(new RoleVoter());//角色投票器
        // voters.add(new AllMatchRoleVoter()); 也可以自定义角色投票器

        //基于乐观的访问决策器：只要任一 AccessDecisionVoter 返回肯定的结果，便授予访问权限。
        return new AffirmativeBased(voters);
        //return new MoreThanHalfBased(voters);
    }

    /**
     * 自定义登陆方式，UserDetailsService类核心就是从数据库中获取用户信息
     * @return
     */
    private UserDetailsService customJdbcUserDetailsService() {
        CustomJdbcUserDetailsService userDetailsService = new CustomJdbcUserDetailsService();
        userDetailsService.setUserDao(userDao);
        userDetailsService.setRoleDao(roleDao);
        return userDetailsService;
    }



    /**
     * 登陆成功自定义处理逻辑
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        CustomSavedRequestAwareAuthenticationSuccessHandler customSavedRequestAwareAuthenticationSuccessHandler = new CustomSavedRequestAwareAuthenticationSuccessHandler();
        customSavedRequestAwareAuthenticationSuccessHandler.setDefaultTargetUrl("/index");//登陆成功跳转页面
        customSavedRequestAwareAuthenticationSuccessHandler.setEmailService(emailService);
        customSavedRequestAwareAuthenticationSuccessHandler.setSmsService(smsService);
        customSavedRequestAwareAuthenticationSuccessHandler.setWeChatService(wechatService);
        return customSavedRequestAwareAuthenticationSuccessHandler;
    }

    /**
     * 登陆失败自定义处理逻辑
     */
    @Bean
    public AuthenticationFailureHandler customSimpleUrlAuthenticationFailureHandler() {
        CustomSimpleUrlAuthenticationFailureHandler customSimpleUrlAuthenticationFailureHandler = new CustomSimpleUrlAuthenticationFailureHandler();
        customSimpleUrlAuthenticationFailureHandler.setDefaultFailureUrl("/login_fail");//登陆失败跳转页面
        customSimpleUrlAuthenticationFailureHandler.setEmailService(emailService);
        customSimpleUrlAuthenticationFailureHandler.setSmsService(smsService);
        customSimpleUrlAuthenticationFailureHandler.setWeChatService(wechatService);

        return customSimpleUrlAuthenticationFailureHandler;
    }
    /**
     * 退出成功自定义处理逻辑
     */
    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        CustomLogoutSuccessHandler customLogoutSuccessHandler = new CustomLogoutSuccessHandler();
        customLogoutSuccessHandler.setDefaultTargetUrl("/logout_success");//退出成功跳转页面
        customLogoutSuccessHandler.setEmailService(emailService);
        customLogoutSuccessHandler.setSmsService(smsService);
        customLogoutSuccessHandler.setWeChatService(wechatService);
        return customLogoutSuccessHandler;
    }

    /**
     * 查询并构造数据资源列表
     * 使用 <code>/x/y/**</code> ant风格来进行匹配
     *
     * 注意，此处我们使用的角色的code字段，而不是其id，如使用其id，则需要修改 {@link CustomJdbcUserDetailsService#loadUserByUsername(String)}
     * 中关于设置role的逻辑，需要查询当前用户所拥有的角色的id，而不是其code。目前，正是查询的当前用户
     * 所拥有的角色的code。所以，此处也需要使用code，保持一致。
     */
    private LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> obtainRequestMap() {
        //查询所有角色功能关系表
        List<SysFuncRole> sysFuncRoles = this.funcDao.listFuncRole();

        if (CollectionUtils.isEmpty(sysFuncRoles)) {
            return new LinkedHashMap<>();
        }

        //按url资源分组<url, role列表>
        Map<String, Set<String>> urlRoleMap = new HashMap<>();

        for (SysFuncRole sysFuncRole : sysFuncRoles) {
            String url = determineAntUrl(sysFuncRole.getUrl());

            Set<String> configAttributes = urlRoleMap.get(url);

            if (configAttributes == null) {
                configAttributes = new HashSet<>();
            }

            configAttributes.add(sysFuncRole.getRoleCode());
            urlRoleMap.put(url, configAttributes);
        }

        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();

        for(String url : urlRoleMap.keySet()) {
            Set<String> needRoles = urlRoleMap.get(url);

            // 注意此处，我们设置ConfigAttribute为 ROLE_ 前缀加上角色标识，
            // 与 CustomJdbcUserDetailsService 里面组织UserDetails设置角色标识呼应
            requestMap.put(new AntPathRequestMatcher(url),
                    needRoles.stream().map(role -> new SecurityConfig("ROLE_" + role)).collect(Collectors.toSet())
            );
        }

        return requestMap;
    }

    /**
     * 去掉最后一个 <code>/</code> 后的内容，以 <code>**</code> 代替，以匹配 <code>ant</code> 风格。
     * @param url 功能地址
     * @return ant风格地址
     */
    private String determineAntUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        return url.substring(0, url.lastIndexOf("/")) + "/**";
    }
}
