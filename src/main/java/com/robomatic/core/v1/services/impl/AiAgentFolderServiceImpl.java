package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.AiAgentFolderEntity;
import com.robomatic.core.v1.repositories.AiAgentFolderRepository;
import com.robomatic.core.v1.services.AiAgentFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiAgentFolderServiceImpl implements AiAgentFolderService {

    @Autowired
    private AiAgentFolderRepository aiAgentFolderRepository;

    @Autowired
    private com.robomatic.core.v1.models.UserModel user;

    @Override
    public AiAgentFolderEntity createFolder(String name, Integer parentFolderId) {
        AiAgentFolderEntity entity = AiAgentFolderEntity.builder()
                .name(name)
                .parentId(parentFolderId != null ? parentFolderId : 0)
                .userId(user.getId())
                .build();
        return aiAgentFolderRepository.save(entity);
    }


    @Override
    public void deleteFolder(Integer id) {
        aiAgentFolderRepository.deleteById(id);
    }
}
