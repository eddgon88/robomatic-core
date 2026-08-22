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

import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.models.PermissionModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ActionRelationalRepository actionRelationalRepository;

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

    @Override
    public List<PermissionModel> getTestPermissions(Integer testId) {
        log.info("Getting permissions for test {}", testId);
        List<ActionRelationalEntity> actions = actionRelationalRepository.findPermissionsByTestId(testId);
        return actions.stream()
                .map(this::mapToPermissionModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionModel> getFolderPermissions(Integer folderId) {
        log.info("Getting permissions for folder {}", folderId);
        List<ActionRelationalEntity> actions = actionRelationalRepository.findPermissionsByFolderId(folderId);
        return actions.stream()
                .map(this::mapToPermissionModel)
                .collect(Collectors.toList());
    }

    @Override
    public void revokePermission(Integer actionId) {
        log.info("Revoking permission with action ID {}", actionId);
        
        ActionEntity action = actionRepository.findById(actionId)
                .orElseThrow(() -> new BadRequestException(BadRequestErrorCode.E400001)); // Or generic not found

        // Validar que sea un permiso (5, 6, 7)
        if (action.getActionId() == 1 || action.getActionId() == 2 || action.getActionId() == 3 || action.getActionId() == 4) {
             throw new BadRequestException(BadRequestErrorCode.E400001); // No se pueden borrar acciones de sistema/owner
        }
        
        actionRepository.delete(action);
    }

    private PermissionModel mapToPermissionModel(ActionRelationalEntity entity) {
        String permission = getPermissionName(entity.getActionId());
        
        return PermissionModel.builder()
                .id(entity.getId())
                .userId(entity.getUserTo().getId())
                .userFullName(entity.getUserTo().getFullName())
                .userEmail(entity.getUserTo().getEmail())
                .permission(permission)
                .build();
    }

    private String getPermissionName(Integer actionId) {
        if (actionId.equals(ActionEnum.EXECUTE_PERMISSION.getCode())) {
            return "execute";
        } else if (actionId.equals(ActionEnum.EDIT_PERMISSION.getCode())) {
            return "edit";
        } else if (actionId.equals(ActionEnum.VIEW_PERMISSION.getCode())) {
            return "view";
        }
        return "unknown";
    }

}
