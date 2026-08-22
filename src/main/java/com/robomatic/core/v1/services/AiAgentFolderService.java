package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.AiAgentFolderEntity;

public interface AiAgentFolderService {
    AiAgentFolderEntity createFolder(String name, Integer parentFolderId);
    void deleteFolder(Integer id);
}
