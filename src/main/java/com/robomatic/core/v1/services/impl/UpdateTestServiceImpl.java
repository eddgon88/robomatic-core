package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.exceptions.ForbiddenException;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.UpdateTestRequestModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.ActionService;
import com.robomatic.core.v1.services.UpdateTestCaseService;
import com.robomatic.core.v1.services.UpdateTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.robomatic.core.v1.exceptions.messages.ForbiddenErrorCode.E403002;
import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500004;
import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404002;

@Slf4j
@Service
public class UpdateTestServiceImpl implements UpdateTestService {

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private UpdateTestCaseService updateTestCaseService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private UserModel currentUser;

    @Override
    public TestEntity updateTest(UpdateTestRequestModel updateTestRequest) {
        Integer testId = updateTestRequest.getId();
        Integer userId = currentUser.getId();
        Integer roleId = currentUser.getRoleId();
        
        // Determinar el nivel de permiso del usuario
        PermissionLevel permissionLevel = getPermissionLevel(testId, userId, roleId);
        
        if (permissionLevel == PermissionLevel.NONE) {
            throw new ForbiddenException(E403002, "You don't have permission to edit this test");
        }

        try {
            TestEntity testEntity = testRepository.findById(testId)
                    .orElseThrow(() -> new NotFoundException(E404002));

            // Ejecutores solo pueden actualizar test_cases (y credenciales via otro endpoint)
            // NO pueden modificar: name, description, threads, web, scripts
            if (permissionLevel == PermissionLevel.EXECUTE_ONLY) {
                log.info("User {} has EXECUTE_ONLY permission. Only updating test_cases for test {}", userId, testId);
                // Solo actualizar test cases, mantener los demás campos intactos
                updateTestCaseService.updateTestCase(updateTestRequest.getTestCaseId(),
                        updateTestRequest.getTestCases());
            } else {
                // FULL_EDIT: Owner, Edit permission, Admin, Analyst
                testEntity.setFolderId(updateTestRequest.getFolderId());
                testEntity.setName(updateTestRequest.getName());
                testEntity.setScript(updateTestRequest.getScript());
                testEntity.setBeforeScript(updateTestRequest.getBeforeScript());
                testEntity.setAfterScript(updateTestRequest.getAfterScript());
                testEntity.setThreads(updateTestRequest.getThreads());
                testEntity.setWeb(updateTestRequest.isWeb());
                testEntity.setDescription(updateTestRequest.getDescription());

                updateTestCaseService.updateTestCase(updateTestRequest.getTestCaseId(),
                        updateTestRequest.getTestCases());
                        
                testEntity = testRepository.save(testEntity);
            }

            actionService.createAction(1, null, ActionEnum.UPDATE.getCode(), null, testEntity.getId(), null);

            return testEntity;
        } catch (ForbiddenException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalErrorException(E500004, String.format("Conflicts: %s", e.getMessage()));
        }
    }

    /**
     * Niveles de permiso para edición
     */
    private enum PermissionLevel {
        NONE,           // Sin permisos
        EXECUTE_ONLY,   // Solo puede editar test_cases y credenciales
        FULL_EDIT       // Puede editar todo
    }

    /**
     * Determina el nivel de permiso del usuario sobre un test
     */
    private PermissionLevel getPermissionLevel(Integer testId, Integer userId, Integer roleId) {
        // Los ADMIN y ANALYST tienen permiso completo
        if (roleId != null && 
            (roleId.equals(RoleEnum.ADMIN.getCode()) || roleId.equals(RoleEnum.ANALYST.getCode()))) {
            return PermissionLevel.FULL_EDIT;
        }

        // Verificar si es owner o tiene permiso de edición
        if (actionRepository.canUserEditTest(testId, userId)) {
            return PermissionLevel.FULL_EDIT;
        }

        // Verificar si tiene permiso de ejecución
        if (actionRepository.hasExecutePermission(testId, userId)) {
            return PermissionLevel.EXECUTE_ONLY;
        }

        return PermissionLevel.NONE;
    }

}
