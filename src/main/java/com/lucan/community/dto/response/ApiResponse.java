package com.lucan.community.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class ApiResponse {

    private String message;
    private Object data;
}