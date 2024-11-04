package com.yfckevin.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigProperties {
    @Value("${mongo.external.db1.uri}")
    private String badmintonMongoUri;
    @Value("${mongo.external.db2.uri}")
    private String bingBaoMongoUri;
    @Value("${config.badmintonDomain}")
    private String badmintonDomain;
    @Value("${spring.redis.host}")
    private String redisDomain;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${config.globalDomain}")
    public String globalDomain;
    @Value("${spring.security.oauth2.client.registration.line.client-id}")
    public String clientId;

    @Value("${spring.security.oauth2.client.registration.line.client-secret}")
    public String clientSecret;

    @Value("${spring.security.oauth2.client.provider.line.token-uri}")
    public String tokenUri;

    @Value("${spring.security.oauth2.client.provider.line.user-info-uri}")
    public String userInfoUri;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getGlobalDomain() {
        return globalDomain;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public String getBadmintonDomain() {
        return badmintonDomain;
    }

    public void setBadmintonDomain(String badmintonDomain) {
        this.badmintonDomain = badmintonDomain;
    }

    public String getBadmintonMongoUri() {
        return badmintonMongoUri;
    }

    public void setBadmintonMongoUri(String badmintonMongoUri) {
        this.badmintonMongoUri = badmintonMongoUri;
    }

    public String getBingBaoMongoUri() {
        return bingBaoMongoUri;
    }

    public void setBingBaoMongoUri(String bingBaoMongoUri) {
        this.bingBaoMongoUri = bingBaoMongoUri;
    }

    public String getRedisDomain() {
        return redisDomain;
    }

    public void setRedisDomain(String redisDomain) {
        this.redisDomain = redisDomain;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }
}
