package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private Integer id;
    private String fullName;
    private Integer roleId;
    private String phone;
    private String email;
    private boolean enabled;

}
