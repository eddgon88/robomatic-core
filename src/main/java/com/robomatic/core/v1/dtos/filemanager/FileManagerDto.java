package com.robomatic.core.v1.dtos.filemanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileManagerDto {

    private String baseUrl;
    private String webBaseUrl;
    private Endpoint endpoint;

}
