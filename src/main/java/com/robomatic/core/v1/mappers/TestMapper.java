package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.models.CreateTestRequestModel;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;
import com.robomatic.core.v1.models.UpdateTestRequestModel;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TestMapper {

    private static final String PREFIX = "T";

    public TestEntity createTestEntity(CreateTestRequestModel createTestRequest) {

        return TestEntity.builder()
                .testId(RobomaticStringUtils.createRandomId(PREFIX))
                .name(createTestRequest.getName())
                .script(createTestRequest.getScript())
                .beforeScript(createTestRequest.getBeforeScript())
                .afterScript(createTestRequest.getAfterScript())
                .threads(createTestRequest.getThreads())
                .folderId(createTestRequest.getFolderId())
                .web(createTestRequest.isWeb())
                .description(createTestRequest.getDescription())
                .build();

    }

    public TestEntity updateTestEntity(UpdateTestRequestModel updateTestRequest) {

        return TestEntity.builder()
                .id(updateTestRequest.getId())
                .name(updateTestRequest.getName())
                .script(updateTestRequest.getScript())
                .threads(updateTestRequest.getThreads())
                .web(updateTestRequest.isWeb())
                .build();

    }

    public TestModel getTestModel(TestEntity testEntity, TestCaseEntity testCaseEntity) {

        return TestModel.builder()
                .id(testEntity.getId())
                .testId(testEntity.getTestId())
                .name(testEntity.getName())
                .web(testEntity.isWeb())
                .folderId(testEntity.getFolderId())
                .script(testEntity.getScript())
                .beforeScript(testEntity.getBeforeScript())
                .afterScript(testEntity.getAfterScript())
                .threads(testEntity.getThreads())
                .testCaseId(testCaseEntity.getId())
                .description(testEntity.getDescription())
                .build();

    }

    public RecordModel testToRecord(TestEntity testEntity, String permission, String name) {
        return RecordModel.builder()
                .id(testEntity.getId())
                .recordId(testEntity.getTestId())
                .type("test")
                .folderId(testEntity.getFolderId())
                .name(testEntity.getName())
                .permissions(permission)
                .user(name)
                .web(testEntity.isWeb())
                .build();
    }

    public RecordModel testAndActionToRecord(TestEntity testEntity, ActionRelationalEntity action, String permission, Boolean isRunning) {
        return RecordModel.builder()
                .id(testEntity.getId())
                .recordId(testEntity.getTestId())
                .type("test")
                .folderId(testEntity.getFolderId())
                .name(testEntity.getName())
                .permissions(permission)
                .user(action.getUserFrom().getFullName())
                .lastUpdate(action.getDate())
                .isRunning(isRunning)
                .web(testEntity.isWeb())
                .build();
    }


    public RecordModel folderToRecord(FolderEntity folderEntity) {
        return RecordModel.builder()
                .id(folderEntity.getId())
                .type("folder")
                .folderId(folderEntity.getFolderId())
                .name(folderEntity.getName())
                .build();
    }

    public RecordModel folderToRecord(FolderEntity folderEntity, String permission, String userName) {
        return RecordModel.builder()
                .id(folderEntity.getId())
                .type("folder")
                .folderId(folderEntity.getFolderId())
                .name(folderEntity.getName())
                .permissions(permission)
                .user(userName)
                .build();
    }

}
