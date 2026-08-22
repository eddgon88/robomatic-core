package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.FileManagerClient;
import com.robomatic.core.v1.models.EvidenceModel;
import com.robomatic.core.v1.models.EvidenceNameModel;
import com.robomatic.core.v1.services.EvidenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EvidenceServiceImpl implements EvidenceService {

    @Autowired
    private FileManagerClient fileManagerClient;

    @Override
    public List<EvidenceModel> getEvidenceList(String testExecutionId) {
        log.info("Getting evidence list");
        try {
            return fileManagerClient.getEvidenceList(testExecutionId);
        } catch (Exception e) {
            log.error("Exception getting evidence list - {}", e.getMessage());
            throw e;
        }
    }
    @Override
    public List<EvidenceNameModel> getEvidenceNames(String testExecutionId) {
        log.info("Getting evidence names");
        try {
            return fileManagerClient.getEvidenceNames(testExecutionId);
        } catch (Exception e) {
            log.error("Exception getting evidence names - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public EvidenceModel getEvidenceFile(String testExecutionId, String fileName) {
        log.info("Getting evidence file content");
        try {
            return fileManagerClient.getEvidenceFile(testExecutionId, fileName);
        } catch (Exception e) {
            log.error("Exception getting evidence file - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public byte[] downloadAllEvidences(String testExecutionId) {
        log.info("Downloading all evidences as ZIP");
        try {
            return fileManagerClient.downloadAllEvidences(testExecutionId);
        } catch (Exception e) {
            log.error("Exception downloading all evidences - {}", e.getMessage());
            throw e;
        }
    }
}
