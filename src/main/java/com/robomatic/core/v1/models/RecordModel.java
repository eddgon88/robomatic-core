package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordModel {

    private Integer id;
    private String recordId;
    private String name;
    private Integer folderId;
    private String type;
    private String permissions;

}
