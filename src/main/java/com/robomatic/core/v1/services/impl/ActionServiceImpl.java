package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.services.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public ActionEntity createAction(Integer userFrom, Integer userTo, Integer actionId, Integer folderId, Integer testId, Integer testExecutionId) {

        ActionEntity actionEntity = ActionEntity.builder()
                .actionId(actionId)
                .folderId(folderId)
                .testId(testId)
                .userFrom(userFrom)
                .userTo(userTo)
                .date(LocalDateTime.now())
                .testExecutionId(testExecutionId)
                .build();

        return actionRepository.save(actionEntity);
    }
}
