package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.AiAgentEntity;
import com.robomatic.core.v1.entities.AiAgentFolderEntity;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.models.AiAgentModel;
import com.robomatic.core.v1.models.RecordModel;
import com.robomatic.core.v1.repositories.AiAgentFolderRepository;
import com.robomatic.core.v1.repositories.AiAgentRepository;
import com.robomatic.core.v1.services.AiAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiAgentServiceImpl implements AiAgentService {

    @Autowired
    private AiAgentRepository aiAgentRepository;

    @Autowired
    private AiAgentFolderRepository aiAgentFolderRepository;

    @Autowired
    private com.robomatic.core.v1.repositories.ActionRelationalRepository actionRelationalRepository;

    @Autowired
    private com.robomatic.core.v1.models.UserModel user;

    @Override
    public AiAgentModel createAgent(AiAgentModel model) {
        AiAgentEntity entity = mapToEntity(model);
        entity.setUserId(user.getId()); // Set owner
        if (entity.getFolderId() == null) {
            entity.setFolderId(0); // Default to root folder
        }
        entity = aiAgentRepository.save(entity);
        return mapToModel(entity);
    }


    @Override
    public AiAgentModel updateAgent(AiAgentModel model) {
        AiAgentEntity entity = aiAgentRepository.findById(model.getId())
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));
        
        entity.setName(model.getName());
        entity.setRole(model.getRole());
        entity.setGoal(model.getGoal());
        entity.setBackstory(model.getBackstory());
        entity.setLlm(model.getLlm());
        entity.setCompany(model.getCompany());
        entity.setMaxIterations(model.getMaxIterations());
        entity.setVerbose(model.getVerbose());
        entity.setTemperature(model.getTemperature());
        entity.setFolderId(model.getFolderId());

        if (entity.getFolderId() == null) {
            entity.setFolderId(0); // Default to root folder
        }

        entity = aiAgentRepository.save(entity);
        return mapToModel(entity);
    }


    @Override
    public void deleteAgent(Integer id) {
        aiAgentRepository.deleteById(id);
    }

    @Override
    public AiAgentModel getAgent(Integer id) {
        AiAgentEntity entity = aiAgentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404003));
        return mapToModel(entity);
    }

    @Override
    public List<RecordModel> getAgentRecords(Integer folderId) {
        List<RecordModel> records = new ArrayList<>();
        List<AiAgentFolderEntity> folders;
        List<AiAgentEntity> agents;

        if (user.isSuperAdmin()) {
            folders = aiAgentFolderRepository.findByParentId(folderId);
            agents = aiAgentRepository.findByFolderId(folderId);
        } else {
            // Get shared items and owned items
            List<com.robomatic.core.v1.entities.ActionRelationalEntity> actions = actionRelationalRepository.findActionsByUser(user.getId());
            
            folders = new ArrayList<>();
            agents = new ArrayList<>();

            // Filter folders by owner or direct share
            List<AiAgentFolderEntity> allFoldersInParent = aiAgentFolderRepository.findByParentId(folderId);
            for (AiAgentFolderEntity f : allFoldersInParent) {
                if (f.getUserId().equals(user.getId()) || hasPermission(f.getId(), "folder", actions)) {
                    folders.add(f);
                }
            }

            // Filter agents by owner or direct share
            List<AiAgentEntity> allAgentsInParent = aiAgentRepository.findByFolderId(folderId);
            for (AiAgentEntity a : allAgentsInParent) {
                if (a.getUserId().equals(user.getId()) || hasPermission(a.getId(), "agent", actions)) {
                    agents.add(a);
                }
            }
        }

        // Add Folders to records
        folders.forEach(f -> records.add(RecordModel.builder()
                .id(f.getId())
                .name(f.getName())
                .type("folder")
                .folderId(f.getParentId())
                .permissions(user.isSuperAdmin() || f.getUserId().equals(user.getId()) ? "OWNER" : "VIEW") // Folders are mostly view-only sharing for now
                .build()));

        // Add Agents to records
        agents.forEach(a -> records.add(RecordModel.builder()
                .id(a.getId())
                .name(a.getName())
                .type("test") 
                .folderId(a.getFolderId())
                .permissions(getPermissions(a, user))
                .build()));

        return records;
    }

    private boolean hasPermission(Integer itemId, String type, List<com.robomatic.core.v1.entities.ActionRelationalEntity> actions) {
        return actions.stream().anyMatch(a -> {
            if (type.equals("folder")) {
                return a.getAiAgentFolder() != null && a.getAiAgentFolder().getId().equals(itemId);
            } else {
                return a.getAiAgent() != null && a.getAiAgent().getId().equals(itemId);
            }
        });
    }

    private String getPermissions(AiAgentEntity agent, com.robomatic.core.v1.models.UserModel user) {
        if (user.isSuperAdmin() || agent.getUserId().equals(user.getId())) return "OWNER";
        // Logic for shared permissions (can be extended if action_id is checked)
        return "VIEW"; 
    }


    @Override
    public List<AiAgentModel> getAgentsForTest(Integer testId) {
        return aiAgentRepository.findByTestId(testId).stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AiAgentModel> getAvailableAgents() {
        List<AiAgentEntity> allAvailable;
        if (user.isSuperAdmin()) {
            allAvailable = aiAgentRepository.findAll();
        } else {
            List<com.robomatic.core.v1.entities.ActionRelationalEntity> actions = actionRelationalRepository.findActionsByUser(user.getId());
            
            // Owned items
            allAvailable = new ArrayList<>(aiAgentRepository.findByUserId(user.getId()));
            
            // Shared items
            for (com.robomatic.core.v1.entities.ActionRelationalEntity action : actions) {
                if (action.getAiAgent() != null) {
                    AiAgentEntity sharedAgent = action.getAiAgent();
                    if (allAvailable.stream().noneMatch(a -> a.getId().equals(sharedAgent.getId()))) {
                        allAvailable.add(sharedAgent);
                    }
                }
            }
        }
        return allAvailable.stream().map(this::mapToModel).collect(Collectors.toList());
    }

    private AiAgentEntity mapToEntity(AiAgentModel model) {
        return AiAgentEntity.builder()
                .name(model.getName())
                .role(model.getRole())
                .goal(model.getGoal())
                .backstory(model.getBackstory())
                .llm(model.getLlm())
                .company(model.getCompany())
                .maxIterations(model.getMaxIterations())
                .verbose(model.getVerbose())
                .temperature(model.getTemperature())
                .folderId(model.getFolderId())
                .build();
    }

    private AiAgentModel mapToModel(AiAgentEntity entity) {
        return AiAgentModel.builder()
                .id(entity.getId())
                .name(entity.getName())

                .role(entity.getRole())
                .goal(entity.getGoal())
                .backstory(entity.getBackstory())
                .llm(entity.getLlm())
                .company(entity.getCompany())
                .maxIterations(entity.getMaxIterations())
                .verbose(entity.getVerbose())
                .temperature(entity.getTemperature())
                .folderId(entity.getFolderId())
                .build();
    }
}
