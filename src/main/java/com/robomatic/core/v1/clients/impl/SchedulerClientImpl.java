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
        String url = buildUrl(schedulerDto.getEndpoint().getCreateJob());
        String jsonBody = gson.toJson(job);
        
        log.info("Calling SCHEDULER API [CREATE]. Url: {}, body: {}", url, jsonBody);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Usar JSON string para asegurar snake_case (Gson) en lugar de JobModel (Jackson)
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            
            JobCreatedModel result = gson.fromJson(response.getBody(), JobCreatedModel.class);
            if (result == null) {
                throw new BadGatewayException("502002", "Job couldn't be created - empty response");
            }
            
            log.info("Job created successfully: {}", job.getJobId());
            return result;
            
        } catch (HttpStatusCodeException e) {
            String errorMsg = StringUtils.defaultIfBlank(e.getResponseBodyAsString(), e.getMessage());
            logAndThrow("CREATE", errorMsg, "502000", job.getJobId());
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            logAndThrow("CREATE", e.getMessage(), "502001", job.getJobId());
        }
        
        return null; // Nunca llega aquí
    }

    @Override
    public JobModel getJobById(String jobId) {
        String url = buildUrl(schedulerDto.getEndpoint().getGetJob(), jobId);
        
        log.info("Calling SCHEDULER API [GET]. Url: {}", url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JobModel job = gson.fromJson(response.getBody(), JobModel.class);
            
            // Verificar si el scheduler retornó "Job not found"
            if (job == null || job.getJobId() == null) {
                String body = response.getBody();
                if (body != null && body.contains("not found")) {
                    throw new BadGatewayException("502012", "Job not found: " + jobId);
                }
                throw new BadGatewayException("502005", "Job couldn't be reached - invalid response");
            }
            
            return job;
            
        } catch (HttpStatusCodeException e) {
            String errorMsg = StringUtils.defaultIfBlank(e.getResponseBodyAsString(), e.getMessage());
            logAndThrow("GET", errorMsg, "502003", jobId);
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            logAndThrow("GET", e.getMessage(), "502004", jobId);
        }
        
        return null; // Nunca llega aquí
    }

    @Override
    public JobListModel getJobs() {
        String url = buildUrl(schedulerDto.getEndpoint().getGetJobs());
        
        log.info("Calling SCHEDULER API [GET ALL]. Url: {}", url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JobListModel jobs = gson.fromJson(response.getBody(), JobListModel.class);
            
            if (jobs == null) {
                throw new BadGatewayException("502008", "Jobs couldn't be reached - empty response");
            }
            
            return jobs;
            
        } catch (HttpStatusCodeException e) {
            String errorMsg = StringUtils.defaultIfBlank(e.getResponseBodyAsString(), e.getMessage());
            logAndThrow("GET ALL", errorMsg, "502006", url);
        } catch (BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            logAndThrow("GET ALL", e.getMessage(), "502007", url);
        }
        
        return null; // Nunca llega aquí
    }

    @Override
    public JobCreatedModel deleteJob(String jobId) {
        // FIX: Usar getDeleteJob() en lugar de getGetJob()
        String url = buildUrl(schedulerDto.getEndpoint().getDeleteJob(), jobId);
        
        log.info("Calling SCHEDULER API [DELETE]. Url: {}", url);

        try {
            restTemplate.delete(url);
            
            log.info("Job deleted successfully: {}", jobId);
            return JobCreatedModel.builder()
                    .scheduled(false)
                    .jobId(jobId)
                    .build();
                    
        } catch (HttpStatusCodeException e) {
            String errorMsg = StringUtils.defaultIfBlank(e.getResponseBodyAsString(), e.getMessage());
            // Si el job no existe, no es un error crítico para delete
            if (e.getStatusCode().value() == 404 || errorMsg.contains("not found")) {
                log.warn("Job {} not found in scheduler, may have been already deleted", jobId);
                return JobCreatedModel.builder()
                        .scheduled(false)
                        .jobId(jobId)
                        .build();
            }
            logAndThrow("DELETE", errorMsg, "502009", jobId);
        } catch (Exception e) {
            logAndThrow("DELETE", e.getMessage(), "502010", jobId);
        }
        
        return null; // Nunca llega aquí
    }

    /**
     * Construye la URL completa para el endpoint
     */
    private String buildUrl(String endpoint) {
        return StringUtils.join(schedulerDto.getBaseUrl(), endpoint);
    }

    /**
     * Construye la URL reemplazando el placeholder del jobId
     */
    private String buildUrl(String endpoint, String jobId) {
        return StringUtils.join(schedulerDto.getBaseUrl(), endpoint.replace("##jobId##", jobId));
    }

    /**
     * Log del error y lanza excepción
     */
    private void logAndThrow(String operation, String message, String code, String reference) {
        String error = LogUtils.formatObjectToJson(
                String.format("Scheduler API [%s]", operation),
                String.format("Error: %s", message),
                reference
        );
        log.error(error);
        throw new BadGatewayException(code, message);
    }
}
