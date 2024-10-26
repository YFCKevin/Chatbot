package com.yfckevin.chatbot.utils;

import java.util.Random;

public class ChatUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String genChannelNum() {
        StringBuilder channel = new StringBuilder("C-");

        // 產生8位的隨機字母和數字
        for (int i = 0; i < 8; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            channel.append(CHARACTERS.charAt(randomIndex));
        }

        return channel.toString();
    }
}
