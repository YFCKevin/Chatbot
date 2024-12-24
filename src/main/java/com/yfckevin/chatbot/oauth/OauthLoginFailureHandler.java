package com.yfckevin.chatbot.oauth;

import com.yfckevin.chatbot.ConfigProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *  第三方登入失敗或取消會進來處理
 */
@Component
public class OauthLoginFailureHandler implements AuthenticationFailureHandler {
    protected Logger logger = LoggerFactory.getLogger(OauthLoginFailureHandler.class);
    private final ConfigProperties configProperties;

    public OauthLoginFailureHandler(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
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
