package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.models.ShareTestRequest;
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

    @Override
    public ActionEntity shareTest(ShareTestRequest shareTestRequest, Integer currentUserId) {
        // Determinar si es folder o test
        boolean isFolder = shareTestRequest.isFolder();
        
        log.info("Sharing {} {} with user {} with permission {}", 
                isFolder ? "folder" : "test",
                isFolder ? shareTestRequest.getFolderId() : shareTestRequest.getTestId(), 
                shareTestRequest.getUserToId(), 
                shareTestRequest.getPermissionType());

        // Mapear el tipo de permiso al código de acción correspondiente
        // Para folders, siempre usar VIEW_PERMISSION
        Integer actionId;
        if (isFolder) {
            actionId = ActionEnum.VIEW_PERMISSION.getCode();
        } else {
            actionId = mapPermissionTypeToActionId(shareTestRequest.getPermissionType());
        }

        return createAction(
                currentUserId,
                shareTestRequest.getUserToId(),
                actionId,
                isFolder ? shareTestRequest.getFolderId() : null,
                isFolder ? null : shareTestRequest.getTestId(),
                null
        );
    }

    /**
     * Mapea el tipo de permiso (string) al código de ActionEnum correspondiente
     * 
     * @param permissionType "execute", "view" o "edit"
     * @return código de acción
     */
    private Integer mapPermissionTypeToActionId(String permissionType) {
        return switch (permissionType.toLowerCase()) {
            case "execute" -> ActionEnum.EXECUTE_PERMISSION.getCode();
            case "view" -> ActionEnum.VIEW_PERMISSION.getCode();
            case "edit" -> ActionEnum.EDIT_PERMISSION.getCode();
            default -> throw new BadRequestException(BadRequestErrorCode.E400001);
        };
    }

}
