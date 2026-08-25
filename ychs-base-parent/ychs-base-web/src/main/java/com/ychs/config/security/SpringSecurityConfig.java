package com.ychs.config.security;

import com.ychs.config.security.detailservice.CustomerUserDetailService;
import com.ychs.config.security.filter.CheckTokenFilter;
import com.ychs.config.security.handler.CustomAccessDeineHandler;
import com.ychs.config.security.handler.LoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Configuration: 表明SpringSecurityConfig类是一个配置类
 * @EnableWebSecurity：启动springsecurity
 * @EnableGlobalMethodSecurity(prePostEnabled = true) : 启用springsecurity的注解
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    @Autowired
    private CustomerUserDetailService customerUserDetailService;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private CustomAccessDeineHandler customAccessDeineHandler;
    @Autowired
    private CheckTokenFilter checkTokenFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 恢复token校验：CheckTokenFilter 解析请求头里的 token，把登录用户放进安全上下文，
        // @PreAuthorize 才能判断权限。之前为"方便前端测试"把它注释掉，导致带 @PreAuthorize 的接口
        // (新增/编辑/删除/重置密码) 全部因"无认证身份"而失效
        http
                //解决跨域
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers((headers) -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::disable)))
                //无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 鉴权白名单, 配置绕过鉴权的接口
                .authorizeHttpRequests((authorized ) ->authorized
                        // 这里过滤一些 不需要token的接口地址
                        .requestMatchers("/api/sysUser/getImage", "/api/sysUser/login","/api/upload/uploadImage","/images/**").permitAll()
                        // 其余接口统一放行到 URL 层，具体权限由方法级 @PreAuthorize 控制
                        .anyRequest().permitAll()
                )
                //指定 登录鉴权时 查询用户信息的实现类
                .userDetailsService(customerUserDetailService)
                // 自定义异常处理
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(loginFailureHandler) // 匿名处理
                        .accessDeniedHandler(customAccessDeineHandler)  // 无权限处理
                )
                // 注册token校验过滤器：在 UsernamePasswordAuthenticationFilter 之前执行
                .addFilterBefore(checkTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 构建过滤链并返回
        return http.build();
    }

    //注入AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
