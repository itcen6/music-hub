package com.weatherdecen.musichub.common.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ChatGPTRequest {

    private String model;
    private List<Message> messages;

    public static ChatGPTRequest of(String prompt, String model) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", prompt));

        return new ChatGPTRequest(model, messages);
    }
}