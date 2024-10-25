package com.yfckevin.chatbot.function;

import java.util.Date;
import java.util.function.Function;

public class CurrectDateTimeFunction implements Function<CurrectDateTimeFunction.Request, CurrectDateTimeFunction.Response>{
    @Override
    public Response apply(Request request) {
        return new Response(new Date());
    }
    public record Request(String State){
    }
    public record Response(Date currDateTime) {
    }
}
