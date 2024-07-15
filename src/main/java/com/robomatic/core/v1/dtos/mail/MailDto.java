package com.robomatic.core.v1.dtos.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {

    private String baseUrl;
    private String templateId;
    private Endpoint endpoint;

}
