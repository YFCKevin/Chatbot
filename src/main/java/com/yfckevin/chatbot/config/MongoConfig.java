package com.yfckevin.chatbot.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.yfckevin.chatbot.ConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {
    private final ConfigProperties configProperties;

    public MongoConfig(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    protected String getDatabaseName() {
        return "chat";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(configProperties.getMongoUri());
    }

    @Bean(name = "localMongoTemplate")
    public MongoTemplate localMongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    @Bean(name = "badmintonMongoTemplate")
    public MongoTemplate externalMongoTemplate1() throws Exception {
        MongoClient externalMongoClient = MongoClients.create(configProperties.getBadmintonMongoUri());
        return new MongoTemplate(externalMongoClient, "badmintonPairing");
    }

    @Bean(name = "bingBaoMongoTemplate")
    public MongoTemplate externalMongoTemplate2() throws Exception {
        MongoClient externalMongoClient = MongoClients.create(configProperties.getBingBaoMongoUri());
        return new MongoTemplate(externalMongoClient, "bingBao");
    }

}
