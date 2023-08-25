package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.PermissionsEnum;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.mappers.TestMapper;
import com.robomatic.core.v1.models.CreateFolderRequestModel;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.repositories.FolderRepository;
import com.robomatic.core.v1.services.ActionService;
import com.robomatic.core.v1.services.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    ActionService actionService;

    @Override
    public RecordModel createFolder(CreateFolderRequestModel createFolderRequestModel) {
        try {
            FolderEntity folder = folderRepository.save(FolderEntity.builder()
                    .folderId(createFolderRequestModel.getFolderId())
                    .name(createFolderRequestModel.getName())
                    .build());
            actionService.createAction(1, null, ActionEnum.CREATE.getCode(), folder.getId(), null, null);
            return testMapper.folderToRecord(folder);
        } catch (Exception e) {
            log.error("Error crating folder: {}", e.getMessage());
            throw new BadRequestException(BadRequestErrorCode.E400002);
        }

    }

    @Override
    public FolderEntity deleteFolder(Integer folderId) {
        FolderEntity folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404007));
        folderRepository.delete(folder);

        return folder;
    }
}
