package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.models.ShareTestRequest;

public interface ActionService {

    ActionEntity createAction(Integer userFrom, Integer userTo, Integer actionId, Integer folderId, Integer testId, Integer testExecutionId);

    /**
     * Comparte un test con otro usuario otorgándole un tipo específico de permiso
     * 
     * @param shareTestRequest Request con los datos de compartir
     * @param currentUserId ID del usuario que comparte (owner)
     * @return ActionEntity creada
     */
    ActionEntity shareTest(ShareTestRequest shareTestRequest, Integer currentUserId);

}
