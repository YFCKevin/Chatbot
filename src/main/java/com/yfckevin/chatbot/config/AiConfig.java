package com.yfckevin.chatbot.config;

import com.yfckevin.chatbot.function.CurrectDateTimeFunction;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public FunctionCallback getCurrentDateTime (){
        return FunctionCallbackWrapper.builder(new CurrectDateTimeFunction())
                .withName("currentDateTime")
                .withDescription("今天日期時間")
                .withResponseConverter((response) -> response.currDateTime().toString())
                .build();
    }
}
