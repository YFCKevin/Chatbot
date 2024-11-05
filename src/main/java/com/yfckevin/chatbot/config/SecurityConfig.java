package com.yfckevin.chatbot.config;

import com.yfckevin.chatbot.oauth.CustomOAuth2UserService;
import com.yfckevin.chatbot.oauth.OauthLoginFailureHandler;
import com.yfckevin.chatbot.oauth.OauthLoginSuccessHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private CustomOAuth2UserService oauthUserService;

    @Resource
    OauthLoginSuccessHandler oauthLoginSuccessHandler;  //第三方登入成功後會處理的

    @Resource
    OauthLoginFailureHandler oauthLoginFailureHandler;  //第三方登入失敗或取消會進來處理

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().disable();
        http.headers().frameOptions().disable();
        http.csrf(csrf -> csrf.disable());

        http.logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/chat.html")
                .and().oauth2Login().userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler(oauthLoginSuccessHandler)
                .failureHandler(oauthLoginFailureHandler);

        return http.build();
    }

}
