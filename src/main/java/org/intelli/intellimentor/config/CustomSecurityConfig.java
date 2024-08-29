package org.intelli.intellimentor.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.intelli.intellimentor.security.handler.APILoginFailHandler;
import org.intelli.intellimentor.security.handler.APILoginSuccessHandler;
import org.intelli.intellimentor.security.filter.JWTCheckFilter;
import org.intelli.intellimentor.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        log.info("------------------------------security config---------------");
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        http.formLogin(config ->{

            config.loginPage("/api/member/login");
            config.successHandler(new APILoginSuccessHandler());
            config.failureHandler(new APILoginFailHandler());
        });

        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);


        //PreAuth
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 Origin을 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 허용할 HTTP 메서드 지정
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE","OPTIONS"));

        // 모든 헤더를 허용하도록 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 자격 증명(쿠키, 인증 헤더 등)을 허용
        configuration.setAllowCredentials(true);

        // CORS 설정을 적용할 경로 패턴을 지정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }



}
