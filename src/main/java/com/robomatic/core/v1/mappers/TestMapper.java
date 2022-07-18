package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.entities.TestCaseEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.models.CreateTestRequestModel;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.models.TestModel;
import com.robomatic.core.v1.models.UpdateTestRequestModel;
import com.robomatic.core.v1.utils.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TestMapper {

    private static final String PREFIX = "T";

    public TestEntity createTestEntity(CreateTestRequestModel createTestRequest) {

        return TestEntity.builder()
                .testId(StringUtils.createRandomId(PREFIX))
                .name(createTestRequest.getName())
                .script(createTestRequest.getScript())
                .threads(createTestRequest.getThreads())
                .folderId(createTestRequest.getFolderId())
                .web(createTestRequest.isWeb())
                .build();

    }

    public TestEntity UpdateTestEntity(UpdateTestRequestModel updateTestRequest) {

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
                .threads(testEntity.getThreads())
                .testCaseId(testCaseEntity.getId())
                .build();

    }

    public RecordModel testToRecord(TestEntity testEntity, String permission) {
        return RecordModel.builder()
                .id(testEntity.getId())
                .recordId(testEntity.getTestId())
                .type("test")
                .folderId(testEntity.getFolderId())
                .name(testEntity.getName())
                .permissions(permission)
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

}
