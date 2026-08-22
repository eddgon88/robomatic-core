package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.ExecutionCountEntity;
import com.robomatic.core.v1.models.ExecutionCountMessage;
import com.robomatic.core.v1.models.TestExecutionModel;
import com.robomatic.core.v1.repositories.ExecutionCountRepository;
import com.robomatic.core.v1.services.ExecutionCounterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class ExecutionCounterServiceImpl implements ExecutionCounterService {

    @Autowired
    private ExecutionCountRepository executionCountRepository;

    @Override
    public void retrieveAndAttachCounterData(Integer testId, TestExecutionModel model) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        Optional<ExecutionCountEntity> counterOpt = executionCountRepository.findByTestIdAndYearAndMonth(testId, year, month);

        if (counterOpt.isPresent()) {
            ExecutionCountEntity counter = counterOpt.get();
            model.setMaxExecutions(counter.getMaxExecutions());
            model.setCurrentExecutions(counter.getCurrentExecutions());
        } else {
            model.setMaxExecutions(0); // 0 means unlimited by default if no record exists
            model.setCurrentExecutions(0);
        }
    }

    @Override
    @Transactional
    public void incrementExecutionCount(ExecutionCountMessage message) {
        log.info("Incrementing execution count for testId: {}, year: {}, month: {}", 
                message.getTestId(), message.getYear(), message.getMonth());
        
        Optional<ExecutionCountEntity> counterOpt = executionCountRepository.findByTestIdAndYearAndMonth(
                message.getTestId(), message.getYear(), message.getMonth());

        if (counterOpt.isPresent()) {
            ExecutionCountEntity counter = counterOpt.get();
            counter.setCurrentExecutions(counter.getCurrentExecutions() + 1);
            executionCountRepository.save(counter);
            log.info("New count: {}", counter.getCurrentExecutions());
        } else {
            log.warn("No counter record found for testId: {}, year: {}, month: {}. Skipping increment.", 
                    message.getTestId(), message.getYear(), message.getMonth());
        }
    }

    @Override
    public ExecutionCountEntity getCounterData(Integer testId, Integer year, Integer month) {
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            if (year == null) year = now.getYear();
            if (month == null) month = now.getMonthValue();
        }

        return executionCountRepository.findByTestIdAndYearAndMonth(testId, year, month)
                .orElse(ExecutionCountEntity.builder()
                        .testId(testId)
                        .year(year)
                        .month(month)
                        .maxExecutions(0)
                        .currentExecutions(0)
                        .build());
    }

    @Override
    @Transactional
    public void updateMaxExecutions(Integer testId, Integer maxExecutions, Integer year, Integer month) {
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            if (year == null) year = now.getYear();
            if (month == null) month = now.getMonthValue();
        }

        ExecutionCountEntity counter = executionCountRepository.findByTestIdAndYearAndMonth(testId, year, month)
                .orElse(ExecutionCountEntity.builder()
                        .testId(testId)
                        .year(year)
                        .month(month)
                        .currentExecutions(0)
                        .build());

        counter.setMaxExecutions(maxExecutions);
        executionCountRepository.save(counter);
        log.info("Updated max executions for testId: {} for {}/{} to {}", testId, month, year, maxExecutions);
    }
}
