package com.weatherdecen.musichub.common.dto;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Message {
    private String role;
    private String content;
}