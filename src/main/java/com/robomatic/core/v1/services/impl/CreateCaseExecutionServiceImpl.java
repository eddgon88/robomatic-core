package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.CaseExecutionEntity;
import com.robomatic.core.v1.mappers.CaseExecutionMapper;
import com.robomatic.core.v1.models.CreateCaseExecutionRequestModel;
import com.robomatic.core.v1.repositories.CaseExecutionRepository;
import com.robomatic.core.v1.services.CreateCaseExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateCaseExecutionServiceImpl implements CreateCaseExecutionService {

    @Autowired
    private CaseExecutionRepository caseExecutionRepository;

    @Autowired
    private CaseExecutionMapper caseExecutionMapper;

    @Override
    public CaseExecutionEntity createCaseExecution(CreateCaseExecutionRequestModel createCaseExecutionRequest) {

        CaseExecutionEntity caseExecutionEntity = caseExecutionMapper.createCaseExecutionEntity(createCaseExecutionRequest);

        return caseExecutionRepository.save(caseExecutionEntity);
    }
}
