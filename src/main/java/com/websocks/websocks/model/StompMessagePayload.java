package com.websocks.websocks.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StompMessagePayload {
    private String message;
    private String id;
    private Integer count;
    private List<String> users;
    private String filename;
}
