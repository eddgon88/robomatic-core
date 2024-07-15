package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMailRequest {

    private List<String> email;
    private String subject;
    private String body;
    private Map<String,String> bodyDict;
    private String templateId;
    private List<String> files;

}
