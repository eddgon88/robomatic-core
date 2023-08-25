package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.FolderEntity;
import com.robomatic.core.v1.models.CreateFolderRequestModel;
import com.robomatic.core.v1.models.RecordModel;

public interface FolderService {

    RecordModel createFolder(CreateFolderRequestModel createFolderRequestModel);

    FolderEntity deleteFolder(Integer folderId);

}
