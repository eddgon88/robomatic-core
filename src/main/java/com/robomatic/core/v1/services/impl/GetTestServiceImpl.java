package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ActionEntity;
import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.PermissionsEnum;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.FolderRepository;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.GetTestService;
import com.robomatic.core.v1.services.R2StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.E404001;

@Slf4j
@Service
public class GetTestServiceImpl implements GetTestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private R2StorageService r2StorageService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRelationalRepository actionRelationalRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private UserModel user;

    @Override
    public List<RecordModel> getTests(Integer folderId) {
        return getTestList(folderId);
    }

    public List<RecordModel> getTestList(Integer folderId) {
        List<RecordModel> records = new java.util.ArrayList<>();
        List<TestEntity> tests;
        List<FolderEntity> folders;
        List<ActionRelationalEntity> actions;

        if (user.isSuperAdmin()) {
            tests = testRepository.findByFolderId(folderId);
            folders = folderRepository.findByFolderId(folderId);
            actions = new java.util.ArrayList<>(); // Empty for super admin, fillRecords will handle it
        } else {
            actions = actionRelationalRepository.findActionsByUserAndFolder(user.getId(), folderId);
            tests = new java.util.ArrayList<>();
            folders = new java.util.ArrayList<>();

            actions.forEach(a -> {
                if (a.getUserFrom().getId().equals(user.getId()) && a.getActionId().equals(ActionEnum.CREATE.getCode()) && a.getTest() != null) {
                    tests.add(a.getTest());
                } else if (a.getUserFrom().getId().equals(user.getId()) && a.getTest() == null && a.getFolder() != null) {
                    folders.add(a.getFolder());
                } else if (a.getUserTo() != null && a.getUserTo().getId().equals(user.getId()) && a.getTest() != null && a.getFolder() == null) {
                    tests.add(a.getTest());
                } else if (a.getUserTo() != null && a.getUserTo().getId().equals(user.getId()) && a.getTest() == null && a.getFolder() != null) {
                    folders.add(a.getFolder());
                }
            });
        }

        fillRecords(records, tests, folders, actions, folderId);
        return records;
    }

    private void fillRecords(List<RecordModel> records, List<TestEntity> tests, List<FolderEntity> folders, List<ActionRelationalEntity> actions, Integer folderId) {
        List<TestEntity> folderTests = tests.stream()
                .filter(t -> t.getFolderId().equals(folderId))
                .collect(Collectors.toList());

        Set<Integer> testIds = folderTests.stream()
                .map(TestEntity::getId)
                .collect(Collectors.toSet());

        Set<Integer> runningTestIds = testIds.isEmpty()
                ? Collections.emptySet()
                : testExecutionRepository.findRunningTestIds(testIds);

        folderTests.forEach(t -> {
            ActionRelationalEntity action = actions.stream().filter(a -> a.getTest() != null && a.getTest().getId().equals(t.getId())).findFirst().orElse(null);
            Boolean isRunning = runningTestIds.contains(t.getId());
            
            String folderName = null; 
            
            if (user.isSuperAdmin()) {
                records.add(testMapper.testAndActionToRecord(t, null, PermissionsEnum.OWNER.getValue(), isRunning, folderName));
            } else if (action != null) {
                if (action.getActionId().equals(ActionEnum.CREATE.getCode()))
                    records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.OWNER.getValue(), isRunning, folderName));
                if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode()))
                    records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EDIT.getValue(), isRunning, folderName));
                if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode()))
                    records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EXECUTE.getValue(), isRunning, folderName));
                if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode()))
                    records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.VIEW.getValue(), isRunning, folderName));
            }
        });
        getLastExecution(records);
        
        // Procesar folders con sus permisos
        folders.stream().filter(f -> !f.getId().equals(0) && f.getFolderId().equals(folderId))
                .forEach(f -> {
                    if (user.isSuperAdmin()) {
                        records.add(testMapper.folderToRecord(f, PermissionsEnum.OWNER.getValue(), "Super Admin"));
                    } else {
                        ActionRelationalEntity action = actions.stream()
                                .filter(a -> a.getFolder() != null && a.getFolder().getId().equals(f.getId()))
                                .findFirst().orElse(null);
                        
                        if (action != null) {
                            String permission;
                            String userName = action.getUserFrom().getFullName();
                            
                            // Determinar el permiso basado en el actionId
                            if (action.getActionId().equals(ActionEnum.CREATE.getCode())) {
                                permission = PermissionsEnum.OWNER.getValue();
                            } else if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode())) {
                                permission = PermissionsEnum.EDIT.getValue();
                            } else if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode())) {
                                permission = PermissionsEnum.EXECUTE.getValue();
                            } else if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode())) {
                                permission = PermissionsEnum.VIEW.getValue();
                            } else {
                                permission = PermissionsEnum.VIEW.getValue(); // Default
                            }
                            
                            records.add(testMapper.folderToRecord(f, permission, userName));
                        } else {
                            // Fallback: usar el método sin permisos (legacy)
                            records.add(testMapper.folderToRecord(f));
                        }
                    }
                });
    }

    private Boolean checkIsRunning(Integer testId) {
        AtomicReference<Boolean> isRunning = new AtomicReference<>(false);
        testExecutionRepository.findByTestIdAndRunningStatus(testId).ifPresent(t -> isRunning.set(true));
        return isRunning.get();
    }

    private void getLastExecution(List<RecordModel> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Integer> testIds = records.stream()
                .map(RecordModel::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (testIds.isEmpty()) {
            return;
        }

        List<ActionEntity> lastActions = actionRepository.findLastExecutionActionsByTestIds(testIds);
        if (lastActions.isEmpty()) {
            return;
        }

        Map<Integer, ActionEntity> lastActionByTestId = lastActions.stream()
                .collect(Collectors.toMap(ActionEntity::getTestId, a -> a, (a1, a2) -> a1));

        Set<Integer> executionIds = lastActions.stream()
                .map(ActionEntity::getTestExecutionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, TestExecutionEntity> executionsById = executionIds.isEmpty()
                ? Collections.emptyMap()
                : testExecutionRepository.findAllById(executionIds).stream()
                        .collect(Collectors.toMap(TestExecutionEntity::getId, e -> e));

        records.forEach(r -> {
            ActionEntity action = lastActionByTestId.get(r.getId());
            if (action != null) {
                TestExecutionEntity testExecution = executionsById.getOrDefault(
                        action.getTestExecutionId(),
                        TestExecutionEntity.builder().build()
                );
                r.setLastExecution(action.getDate());
                r.setLastExecutionState(StatusEnum.getStatusByCode(testExecution.getStatus()).getValue());
            }
        });
    }

    @Override
    public TestModel getTest(Integer testId) {
        TestEntity testEntity = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404002));

        TestCaseEntity testCaseEntity = testCaseRepository.getDefaultByTestId(testId)
                .orElseThrow(() -> new NotFoundException(E404001));
        TestModel test = testMapper.getTestModel(testEntity, testCaseEntity);
        test.setTestCases(getTestCases(testCaseEntity.getFileDir()));
        
        test.setPermissions(getUserPermissionOnTest(testId));

        return test;
    }

    /**
     * Determina el permiso que tiene el usuario actual sobre un test específico.
     * 
     * Prioridad de permisos:
     * 1. Owner (creador del test)
     * 2. Edit permission
     * 3. Execute permission
     * 4. View permission
     */
    private String getUserPermissionOnTest(Integer testId) {
        if (user.isSuperAdmin()) {
            return PermissionsEnum.OWNER.getValue();
        }
        Integer userId = user.getId();
        
        // Buscar la acción relacionada al usuario específicamente para este test
        List<ActionEntity> actions = actionRepository.findUserActionsOnTest(userId, testId);
        
        for (ActionEntity action : actions) {
            // Es el creador (owner)
            if (action.getActionId().equals(ActionEnum.CREATE.getCode()) && 
                userId.equals(action.getUserFrom())) {
                return PermissionsEnum.OWNER.getValue();
            }
            // Tiene permiso de edición
            if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode()) && 
                userId.equals(action.getUserTo())) {
                return PermissionsEnum.EDIT.getValue();
            }
            // Tiene permiso de ejecución
            if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode()) && 
                userId.equals(action.getUserTo())) {
                return PermissionsEnum.EXECUTE.getValue();
            }
            // Tiene permiso de visualización
            if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode()) && 
                userId.equals(action.getUserTo())) {
                return PermissionsEnum.VIEW.getValue();
            }
        }
        
        // Default: view (aunque no debería llegar aquí si tiene acceso)
        return PermissionsEnum.VIEW.getValue();
    }

    private String getTestCases(String testCaseDir) {
        // 1. Intentar leer directamente desde Cloudflare R2
        if (r2StorageService != null && r2StorageService.isEnabled()) {
            String r2Base64 = r2StorageService.getTestCaseBase64(testCaseDir);
            if (r2Base64 != null) {
                return r2Base64;
            }
        }

        // 2. Fallback a lectura desde disco local
        StringBuilder resultStringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(testCaseDir))) {
            stream.forEach(s -> resultStringBuilder.append(s).append("\n"));
        } catch (Exception e) {
            log.error("Could not find test case file in R2 or local disk: {}", testCaseDir);
            throw new InternalErrorException(InternalErrorCode.E500000);
        }
        byte[] encodedBytes = Base64.getEncoder().encode(resultStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }


    @Override
    public List<RecordModel> getTestsForScheduler() {
        if (user.isSuperAdmin()) {
            List<TestEntity> allTests = testRepository.findAll();
            Set<Integer> testIds = allTests.stream().map(TestEntity::getId).collect(Collectors.toSet());
            Set<Integer> runningTestIds = testIds.isEmpty()
                    ? Collections.emptySet()
                    : testExecutionRepository.findRunningTestIds(testIds);
            Set<Integer> folderIds = allTests.stream()
                    .map(TestEntity::getFolderId)
                    .filter(fid -> fid != null && fid != 0)
                    .collect(Collectors.toSet());
            Map<Integer, String> folderNames = folderIds.isEmpty()
                    ? Collections.emptyMap()
                    : folderRepository.findAllById(folderIds).stream()
                            .collect(Collectors.toMap(FolderEntity::getId, FolderEntity::getName));

            return allTests.stream().map(test -> {
                Boolean isRunning = runningTestIds.contains(test.getId());
                String folderName = "Root";
                if (test.getFolderId() != null && test.getFolderId() != 0) {
                    folderName = folderNames.getOrDefault(test.getFolderId(), "Unknown");
                }
                return testMapper.testAndActionToRecord(test, null, PermissionsEnum.OWNER.getValue(), isRunning, folderName);
            }).collect(Collectors.toList());
        }
        
        List<RecordModel> records = new java.util.ArrayList<>();
        
        // Obtener acciones donde el usuario es owner o tiene permiso de edición o ejecución
        List<ActionRelationalEntity> actions = actionRelationalRepository.findTestsWithSchedulablePermission(user.getId());

        Set<Integer> testIds = actions.stream()
                .map(ActionRelationalEntity::getTest)
                .filter(Objects::nonNull)
                .map(TestEntity::getId)
                .collect(Collectors.toSet());
        Set<Integer> runningTestIds = testIds.isEmpty()
                ? Collections.emptySet()
                : testExecutionRepository.findRunningTestIds(testIds);
        Set<Integer> folderIds = actions.stream()
                .map(ActionRelationalEntity::getTest)
                .filter(Objects::nonNull)
                .map(TestEntity::getFolderId)
                .filter(fid -> fid != null && fid != 0)
                .collect(Collectors.toSet());
        Map<Integer, String> folderNames = folderIds.isEmpty()
                ? Collections.emptyMap()
                : folderRepository.findAllById(folderIds).stream()
                        .collect(Collectors.toMap(FolderEntity::getId, FolderEntity::getName));
        
        // Usar un Set para evitar duplicados (un usuario podría tener múltiples permisos sobre el mismo test)
        java.util.Set<Integer> processedTestIds = new java.util.HashSet<>();
        
        for (ActionRelationalEntity action : actions) {
            if (action.getTest() != null && !processedTestIds.contains(action.getTest().getId())) {
                TestEntity test = action.getTest();
                processedTestIds.add(test.getId());
                
                String permission;
                if (action.getActionId().equals(ActionEnum.CREATE.getCode())) {
                    permission = PermissionsEnum.OWNER.getValue();
                } else if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode())) {
                    permission = PermissionsEnum.EDIT.getValue();
                } else {
                    permission = PermissionsEnum.EXECUTE.getValue();
                }
                
                Boolean isRunning = runningTestIds.contains(test.getId());
                
                String folderName = "Root";
                if (test.getFolderId() != null && test.getFolderId() != 0) {
                    folderName = folderNames.getOrDefault(test.getFolderId(), "Unknown");
                }
                
                records.add(testMapper.testAndActionToRecord(test, action, permission, isRunning, folderName));
            }
        }
        
        return records;
    }

}
