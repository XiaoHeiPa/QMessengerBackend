package org.qbychat.backend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.AuthorizeVO;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.qbychat.backend.filter.JwtAuthorizeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig {
    @Resource
    AccountServiceImpl accountService;
    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter filter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // user requests
                .authorizeHttpRequests(
                        conf -> conf
                                .requestMatchers("/user/login").anonymous()
                                .requestMatchers("/user/register").anonymous()
                                .requestMatchers("/ws").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        conf -> conf
                                .loginProcessingUrl("/api/auth/login")
                                .successHandler(this::onAuthenticationSuccessful)
                                .failureHandler(this::onAuthenticationFailure)
                )
                .logout(
                        conf -> conf
                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(exception).toJson());
    }

    private void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(exception).toJson());
    }

    private void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String auth = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(auth)) {
            // make token invalidate
            writer.write(RestBean.success().toJson());
        } else {
            writer.write(RestBean.failure(400, "Failed to logout").toJson());
        }
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(exception).toJson());
    }

    private void onAuthenticationSuccessful(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        User user = (User) authentication.getPrincipal();
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        String token = jwtUtils.createJwt(user, 1, account.getUsername());
        AuthorizeVO authorizeVO = account.asViewObject(AuthorizeVO.class, authorizeVO1 -> {
            authorizeVO1.setExpire(jwtUtils.getExpireDate());
            authorizeVO1.setToken(token);
        });
        response.getWriter().write(RestBean.success(authorizeVO).toJson());
    }
}
