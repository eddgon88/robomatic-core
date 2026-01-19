package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para representar un usuario en listas (sin información sensible)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListModel {

    private Integer id;
    
    private String fullName;
    
    private String email;

}

