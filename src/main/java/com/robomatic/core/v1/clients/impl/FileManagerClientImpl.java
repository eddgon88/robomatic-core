package com.robomatic.core.v1.clients.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.robomatic.core.v1.clients.FileManagerClient;
import com.robomatic.core.v1.dtos.filemanager.FileManagerDto;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.models.EvidenceModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileManagerClientImpl implements FileManagerClient {

    @Autowired
    private FileManagerDto fileManagerDto;

    @Autowired
    private RestTemplate restTemplate;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public List<EvidenceModel> getEvidenceList(String testExecutionId) {
        List<EvidenceModel> resp = new ArrayList<>();

        String url = StringUtils.join(fileManagerDto.getBaseUrl(), fileManagerDto.getEndpoint().getGetEvidenceList().replace("##testExecutionId##", testExecutionId));

        log.info("Calling FILE-MANAGER API. Url: {}", url);

        try {

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            resp = gson.fromJson(responseEntity.getBody(), new TypeToken<List<EvidenceModel>>() {
            }.getType());
        } catch (Exception e) {
            log.error("Error getting evidences from file-manager-api - {}", e.getMessage());
            throw e;
        }
        if (resp == null) {
            log.error("Error getting evidences from file-manager-api - NULL");
            throw new NotFoundException(NotFoundErrorCode.E404006);
        }

        return resp;
    }
}
