package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model para compartir un test o folder con otro usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareTestRequest {

    /**
     * ID del test a compartir (null si es folder)
     */
    private Integer testId;

    /**
     * ID del folder a compartir (null si es test)
     */
    private Integer folderId;

    /**
     * ID del usuario con quien compartir
     */
    private Integer userToId;

    /**
     * Tipo de permiso a otorgar:
     * - "execute" (5) - Permiso para ejecutar
     * - "view" (6) - Permiso para ver (por defecto para folders)
     * - "edit" (7) - Permiso para editar
     */
    private String permissionType;

    /**
     * Indica si es un folder (true) o un test (false)
     */
    public boolean isFolder() {
        return folderId != null;
    }

}

