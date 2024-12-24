package com.yfckevin.chatbot.oauth;

import com.yfckevin.chatbot.ConfigProperties;
import com.yfckevin.chatbot.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class OauthLoginSuccessHandler implements AuthenticationSuccessHandler {
    protected Logger logger = LoggerFactory.getLogger(OauthLoginSuccessHandler.class);
    private final UserService userService;
    private final ConfigProperties configProperties;

    public OauthLoginSuccessHandler(UserService userService, ConfigProperties configProperties) {
        this.userService = userService;
        this.configProperties = configProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("第三方登入成功後要做的");

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        System.out.println(oauthUser.getOauth2ClientName());

        //處理把第三方的帳號儲存到DB，Oauth2ClientName是GOOGLE、FACEBOOK、LINE.....
        Member member  = userService.processOAuthPostLogin(oauthUser.getEmail(),oauthUser.getName(),oauthUser.getOauth2ClientName());

        Cookie memberCookie = new Cookie("MEMBER_ID", URLEncoder.encode(member.getId(), "UTF-8"));
        memberCookie.setMaxAge(24 * 60 * 60);
        memberCookie.setPath("/");
        response.addCookie(memberCookie);

        String projectName = (String) request.getSession().getAttribute("project");
        final String page = (String) request.getSession().getAttribute("page");
        System.out.println("projectName = " + projectName);
        System.out.println("page = " + page);
        switch (projectName) {
            case "badminton" -> {
                if (StringUtils.isNotBlank(page)) {
                    response.sendRedirect(configProperties.getBadmintonDomain() + page);
                } else {
                    response.sendRedirect(configProperties.getGlobalDomain() + "badminton-chat.html");
                }
            }
            case "bingBao" -> {
                if (StringUtils.isNotBlank(page)) {
                    response.sendRedirect(configProperties.getBingBaoDomain() + page);
                } else {
                    response.sendRedirect(configProperties.getGlobalDomain() + "bing-bao-chat.html");
                }
            }
        }
    }

}
