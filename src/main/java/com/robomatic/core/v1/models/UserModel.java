package com.robomatic.core.v1.models;

import com.robomatic.core.v1.enums.RoleEnum;
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

    public boolean isSuperAdmin() {
        return roleId != null && roleId.equals(RoleEnum.SUPER_ADMIN.getCode());
    }

}
