package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionModel {
    private Integer id; // Action ID
    private Integer userId;
    private String userFullName;
    private String userEmail;
    private String permission;
}
