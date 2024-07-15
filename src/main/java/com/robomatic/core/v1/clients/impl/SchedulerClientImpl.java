package com.robomatic.core.v1.clients.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robomatic.core.v1.clients.SchedulerClient;
import com.robomatic.core.v1.dtos.scheduler.SchedulerDto;
import com.robomatic.core.v1.exceptions.BadGatewayException;
import com.robomatic.core.v1.models.JobCreatedModel;
import com.robomatic.core.v1.models.JobListModel;
import com.robomatic.core.v1.models.JobModel;
import com.robomatic.core.v1.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SchedulerClientImpl implements SchedulerClient {

    @Autowired
    private SchedulerDto schedulerDto;

    @Autowired
    private RestTemplate restTemplate;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public JobCreatedModel createJob(final JobModel job) {

        JobCreatedModel resp = null;

        String url = StringUtils.join(schedulerDto.getBaseUrl(), schedulerDto.getEndpoint().getCreateJob());

        String json = gson.toJson(job);
        log.info("Calling SCHEDULER API. Url: {}, req: {}", url, json);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<JobModel> httpEntity = new HttpEntity<>(job, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            resp = gson.fromJson(responseEntity.getBody(), JobCreatedModel.class);
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502000", job.getJobId());
        } catch (Exception e) {
            throwError(e.getMessage(), "502001", job.getJobId());
        }
        if (resp == null) {
            throwError("job couldn't be created.", "502002", job.getJobId());
        }

        return resp;

    }

    private void throwError(String messageError, String codeError, String referenceId) {
        String error = LogUtils.formatObjectToJson("Send Schedule", String.format("API Error: %s", messageError), referenceId);
        log.error(error);
        throw new BadGatewayException(codeError, messageError);
    }

    @Override
    public JobModel getJobById(String jobId) {

        JobModel job = null;

        String url = StringUtils.join(schedulerDto.getBaseUrl(), schedulerDto.getEndpoint()
                .getGetJob().replace("##jobId##", jobId));

        log.info("Calling SCHEDULER API. Url: {}, job: {}", url, jobId);

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            job = gson.fromJson(responseEntity.getBody(), JobModel.class);
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502003", jobId);
        } catch (Exception e) {
            throwError(e.getMessage(), "502004", jobId);
        }
        if (job == null) {
            throwError("job couldn't be reached", "502005", jobId);
        }
        return job;
    }

    @Override
    public JobListModel getJobs() {
        JobListModel jobs = null;

        String url = StringUtils.join(schedulerDto.getBaseUrl(), schedulerDto.getEndpoint()
                .getGetJobs());

        log.info("Calling SCHEDULER API. Url: {}", url);

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            jobs = gson.fromJson(responseEntity.getBody(), JobListModel.class);
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502006", url);
        } catch (Exception e) {
            throwError(e.getMessage(), "502007", url);
        }
        if (jobs == null) {
            throwError("jobs couldn't be reached", "502008", url);
        }
        return jobs;
    }

    @Override
    public JobCreatedModel deleteJob(String jobId) {
        JobCreatedModel deletedJob = null;

        String url = StringUtils.join(schedulerDto.getBaseUrl(), schedulerDto.getEndpoint()
                .getGetJob().replace("##jobId##", jobId));

        log.info("Calling SCHEDULER API. Url: {}", url);

        try {
            restTemplate.delete(url, String.class);
            deletedJob = JobCreatedModel.builder()
                    .scheduled(false)
                    .jobId(jobId)
                    .build();
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502009", jobId);
        } catch (Exception e) {
            throwError(e.getMessage(), "502010", jobId);
        }
        if (deletedJob == null) {
            throwError("job couldn't be deleted", "502011", jobId);
        }
        return deletedJob;
    }
}
