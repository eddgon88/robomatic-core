package com.robomatic.core.v1.clients.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robomatic.core.v1.clients.TestExecutorClient;
import com.robomatic.core.v1.dtos.executor.ExecutorDto;
import com.robomatic.core.v1.entities.TestExecutionEntity;
import com.robomatic.core.v1.exceptions.BadGatewayException;
import com.robomatic.core.v1.models.TestExecutionModel;
import com.robomatic.core.v1.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TestExecutorClientImpl implements TestExecutorClient {

    @Autowired
    private ExecutorDto executorDto;

    @Autowired
    private RestTemplate restTemplate;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public void executeTest(TestExecutionModel testExecutionModel) {
        String baseUrl = testExecutionModel.isWeb() ? executorDto.getWebBaseUrl() : executorDto.getBaseUrl();
        String url = StringUtils.join(baseUrl, executorDto.getEndpoint().getExecuteTest());

        String json = gson.toJson(testExecutionModel);
        log.info("Calling EXECUTOR API. Url: {}, req: {}", url, json);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TestExecutionModel> httpEntity = new HttpEntity<>(testExecutionModel, headers);

            restTemplate.postForEntity(url, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502000", testExecutionModel.getTestExecutionId());
        } catch (Exception e) {
            throwError(e.getMessage(), "502001", testExecutionModel.getTestExecutionId());
        }
    }

    @Override
    public void stopTestExecution(TestExecutionEntity testExecution) {
        String url = StringUtils.join(executorDto.getBaseUrl(), executorDto.getEndpoint().getStopTestExecution());

        String json = gson.toJson(testExecution);
        log.info("Calling EXECUTOR API. Url: {}, req: {}", url, json);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TestExecutionEntity> httpEntity = new HttpEntity<>(testExecution, headers);

            restTemplate.postForEntity(url, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502000", testExecution.getTestExecutionId());
        } catch (Exception e) {
            throwError(e.getMessage(), "502001", testExecution.getTestExecutionId());
        }
    }

    private void throwError(String messageError, String codeError, String referenceId) {
        String error = LogUtils.formatObjectToJson("Refund Order", String.format("API Error: %s", messageError), referenceId);
        log.error(error);
        throw new BadGatewayException(codeError, messageError);
    }
}
