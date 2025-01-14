package com.yfckevin.chatbot.oauth;

import com.yfckevin.chatbot.ConfigProperties;
import com.yfckevin.chatbot.entity.Member;
import com.yfckevin.chatbot.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

@Controller
public class Oauth2Controller {
    private final ConfigProperties configProperties;
    private final MemberService memberService;
    private final RestTemplate restTemplate;

    public Oauth2Controller(ConfigProperties configProperties, MemberService memberService, RestTemplate restTemplate) {
        this.configProperties = configProperties;
        this.memberService = memberService;
        this.restTemplate = restTemplate;
    }


    @GetMapping("/callback")
    public String handleOAuth2Callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + configProperties.getGlobalDomain() + "callback" +
                "&client_id=" + configProperties.getClientId() +
                "&client_secret=" + configProperties.getClientSecret();

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                configProperties.getTokenUri(),
                HttpMethod.POST,
                requestEntity,
                Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequestEntity = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                configProperties.getUserInfoUri(),
                HttpMethod.GET,
                userRequestEntity,
                Map.class);

        Map<String, Object> userInfo = userResponse.getBody();

        String userId = (String) userInfo.get("userId");
        String userName = (String) userInfo.get("displayName");
        final String pictureUrl = (String) userInfo.get("pictureUrl");

        Optional<Member> memberOpt = memberService.findByUserId(userId);
        Member member;
        if (memberOpt.isEmpty()) {
            member = new Member();
            member.setName(userName);
            member.setPictureUrl(pictureUrl);
            member.setUserId(userId);
            memberService.save(member);
        } else {
            member = memberOpt.get();
        }
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
            case "bingBaoTest" -> {
                if (StringUtils.isNotBlank(page)) {
                    response.sendRedirect(configProperties.getBingBaoTestDomain() + page);
                } else {
                    response.sendRedirect(configProperties.getGlobalDomain() + "bing-bao-chat.html");
                }
            }
        }

        return "";
    }


    /**
     * 登入導向的先行判定，並放入到session
     * @param type google or line
     * @param project 專案名稱
     * @param request
     * @return
     */
    @GetMapping("/login")
    public void login(@RequestParam("type") String type, @RequestParam("project") String project, @RequestParam(defaultValue = "", value = "page") String page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("project = " + project);
        // 將 projectName 存入 session
        HttpSession session = request.getSession();
        session.setAttribute("project", project);
        session.setAttribute("page", page);

        switch (type) {
            case "google" -> response.sendRedirect(configProperties.getGlobalDomain() + "oauth2/authorization/google?project=" + project);
            case "line" -> response.sendRedirect(configProperties.getGlobalDomain() + "oauth2/authorization/line?project=" + project);
        }

    }
}
