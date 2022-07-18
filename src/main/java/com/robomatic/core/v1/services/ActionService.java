package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.ActionEntity;

public interface ActionService {

    ActionEntity createAction(Integer userFrom, Integer userTo, Integer actionId, Integer folderId, Integer testId, Integer testExecutionId);

}
