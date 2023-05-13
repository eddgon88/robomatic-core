package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.PermissionsEnum;
import com.robomatic.core.v1.enums.StatusEnum;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.FolderRepository;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.GetTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private UserRepository userRepository;

    @Autowired
    private ActionRelationalRepository actionRelationalRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Override
    public List<RecordModel> getTests(Integer userId, Integer folderId) {
        /*UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(E404006));
        List<RecordModel> records = new java.util.ArrayList<>();
        List<Integer> testIds = new java.util.ArrayList<>();
        List<Integer> folderIds = new java.util.ArrayList<>();

        List<ActionEntity> actions = actionRepository.findActionsByUser(userId);

        actions.forEach(a -> {
            if (a.getUserFrom().equals(userId) && a.getActionId().equals(ActionEnum.CREATE.getCode()) && a.getTestId() != null) {
                testIds.add(a.getTestId());
            } else if (a.getUserTo().equals(userId) && a.getTestId() != null && a.getFolderId() == null) {
                testIds.add(a.getTestId());
            } else if (a.getUserTo().equals(userId) && a.getTestId() == null && a.getFolderId() != null) {
                folderIds.add(a.getFolderId());
            }
        });
        List<TestEntity> tests = testRepository.findAllById(testIds);
        tests.stream().filter(t -> t.getFolderId().equals(folderId)).forEach(t -> {
            folderIds.add(t.getFolderId());
            ActionEntity action = actions.stream().filter(a -> a.getTestId().equals(t.getId())).findFirst().orElse(null);
            assert action != null;
            if (action.getActionId().equals(ActionEnum.CREATE.getCode())) records.add(testMapper.testToRecord(t, PermissionsEnum.OWNER.getValue(), user.getFullName()));
            if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode())) records.add(testMapper.testToRecord(t, PermissionsEnum.EDIT.getValue(), user.getFullName()));
            if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode())) records.add(testMapper.testToRecord(t, PermissionsEnum.EXECUTE.getValue(), user.getFullName()));
            if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode())) records.add(testMapper.testToRecord(t, PermissionsEnum.VIEW.getValue(), user.getFullName()));
        });
        List<FolderEntity> folders = folderRepository.findAllById(folderIds);
        folders.stream().filter(f -> !f.getId().equals(0) && f.getFolderId().equals(folderId)).forEach(f -> {
            records.add(testMapper.folderToRecord(f));
        });
        return records;*/
        return getTestList(userId, folderId);
    }

    public List<RecordModel> getTestList(Integer userId, Integer folderId) {
        List<RecordModel> records = new java.util.ArrayList<>();
        List<TestEntity> tests = new java.util.ArrayList<>();
        List<FolderEntity> folders = new java.util.ArrayList<>();

        List<ActionRelationalEntity> actions = actionRelationalRepository.findActionsByUser(userId);

        actions.forEach(a -> {
            if (a.getUserFrom().getId().equals(userId) && a.getActionId().equals(ActionEnum.CREATE.getCode()) && a.getTest() != null) {
                tests.add(a.getTest());
            } else if (a.getUserTo().getId().equals(userId) && a.getTest() != null && a.getFolder() == null) {
                tests.add(a.getTest());
            } else if (a.getUserTo().getId().equals(userId) && a.getTest() == null && a.getFolder() != null) {
                folders.add(a.getFolder());
            }
        });

        tests.stream().filter(t -> t.getFolderId().equals(folderId)).forEach(t -> {
            ActionRelationalEntity action = actions.stream().filter(a -> a.getTest().getId().equals(t.getId())).findFirst().orElse(null);
            assert action != null;
            if (action.getActionId().equals(ActionEnum.CREATE.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.OWNER.getValue()));
            if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EDIT.getValue()));
            if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EXECUTE.getValue()));
            if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.VIEW.getValue()));
        });
        getLastExecution(records);
        folders.stream().filter(f -> !f.getId().equals(0) && f.getFolderId().equals(folderId))
                .forEach(f -> records.add(testMapper.folderToRecord(f)));
        return records;
    }

    private List<RecordModel> getLastExecution(List<RecordModel> records) {
        records.forEach(r -> actionRepository.findLastExecutionActionByTestId(r.getId()).ifPresent(action -> {
            TestExecutionEntity testExecution = testExecutionRepository.findById(action.getTestExecutionId()).orElse(TestExecutionEntity.builder().build());
            r.setLastExecution(action.getDate());
            r.setLastExecutionState(StatusEnum.getStatusByCode(testExecution.getStatus()).getValue());
        }));
        return records;
    }

    @Override
    public TestModel getTest(Integer testId) {
        TestEntity testEntity = testRepository.getById(testId);

        TestCaseEntity testCaseEntity = testCaseRepository.getDefaultByTestId(testId)
                .orElseThrow(() -> new NotFoundException(E404001));

        return testMapper.getTestModel(testEntity, testCaseEntity);
    }

}
