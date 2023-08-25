package com.robomatic.core.v1.services.impl;

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
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.FolderRepository;
import com.robomatic.core.v1.repositories.TestCaseRepository;
import com.robomatic.core.v1.repositories.TestExecutionRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.GetTestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    private UserRepository userRepository;

    @Autowired
    private ActionRelationalRepository actionRelationalRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Override
    public List<RecordModel> getTests(Integer userId, Integer folderId) {
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
            } else if (a.getUserFrom().getId().equals(userId) && a.getTest() == null && a.getFolder() != null) {
                folders.add(a.getFolder());
            } else if (a.getUserTo().getId().equals(userId) && a.getTest() != null && a.getFolder() == null) {
                tests.add(a.getTest());
            } else if (a.getUserTo().getId().equals(userId) && a.getTest() == null && a.getFolder() != null) {
                folders.add(a.getFolder());
            }
        });

        fillRecords(records, tests, folders, actions, folderId);
        return records;
    }

    private void fillRecords(List<RecordModel> records, List<TestEntity> tests, List<FolderEntity> folders, List<ActionRelationalEntity> actions, Integer folderId) {

        tests.stream().filter(t -> t.getFolderId().equals(folderId)).forEach(t -> {
            ActionRelationalEntity action = actions.stream().filter(a -> a.getTest().getId().equals(t.getId())).findFirst().orElse(null);
            Boolean isRunning = checkIsRunning(t.getId());
            assert action != null;
            if (action.getActionId().equals(ActionEnum.CREATE.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.OWNER.getValue(), isRunning));
            if (action.getActionId().equals(ActionEnum.EDIT_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EDIT.getValue(), isRunning));
            if (action.getActionId().equals(ActionEnum.EXECUTE_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.EXECUTE.getValue(), isRunning));
            if (action.getActionId().equals(ActionEnum.VIEW_PERMISSION.getCode()))
                records.add(testMapper.testAndActionToRecord(t, action, PermissionsEnum.VIEW.getValue(), isRunning));
        });
        getLastExecution(records);
        folders.stream().filter(f -> !f.getId().equals(0) && f.getFolderId().equals(folderId))
                .forEach(f -> records.add(testMapper.folderToRecord(f)));
    }

    private Boolean checkIsRunning(Integer testId) {
        AtomicReference<Boolean> isRunning = new AtomicReference<>(false);
        testExecutionRepository.findByTestIdAndRunningStatus(testId).ifPresent(t -> isRunning.set(true));
        return isRunning.get();
    }

    private void getLastExecution(List<RecordModel> records) {
        records.forEach(r -> actionRepository.findLastExecutionActionByTestId(r.getId()).ifPresent(action -> {
            TestExecutionEntity testExecution = testExecutionRepository.findById(action.getTestExecutionId()).orElse(TestExecutionEntity.builder().build());
            r.setLastExecution(action.getDate());
            r.setLastExecutionState(StatusEnum.getStatusByCode(testExecution.getStatus()).getValue());
        }));
    }

    @Override
    public TestModel getTest(Integer testId) {
        TestEntity testEntity = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404002));

        TestCaseEntity testCaseEntity = testCaseRepository.getDefaultByTestId(testId)
                .orElseThrow(() -> new NotFoundException(E404001));
        TestModel test = testMapper.getTestModel(testEntity, testCaseEntity);
        test.setTestCases(getTestCases(testCaseEntity.getFileDir()));
        //configurar el usuario cuando se aplique la seguridad JWT
        test.setPermissions(PermissionsEnum.OWNER.getValue());

        return test;
    }

    private String getTestCases(String testCaseDir) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(testCaseDir))) {
            stream.forEach(s -> resultStringBuilder.append(s).append("\n"));
        } catch (Exception e) {
            throw new InternalErrorException(InternalErrorCode.E500000);
        }
        byte[] encodedBytes = Base64.getEncoder().encode(resultStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }

}
