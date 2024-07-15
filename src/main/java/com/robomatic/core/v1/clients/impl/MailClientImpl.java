package com.robomatic.core.v1.clients.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robomatic.core.v1.clients.MailClient;
import com.robomatic.core.v1.dtos.mail.MailDto;
import com.robomatic.core.v1.exceptions.BadGatewayException;
import com.robomatic.core.v1.models.SendMailRequest;
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
public class MailClientImpl implements MailClient {

    @Autowired
    private MailDto mailDto;

    @Autowired
    private RestTemplate restTemplate;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public void sendMail(SendMailRequest req) {

        String url = StringUtils.join(mailDto.getBaseUrl(), mailDto.getEndpoint().getSendMail());

        String json = gson.toJson(req);
        log.info("Calling MAIL API. Url: {}, req: {}", url, json);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SendMailRequest> httpEntity = new HttpEntity<>(req, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            log.info("Request sent to email api - {} - {}", responseEntity.getStatusCode(), responseEntity.getStatusCodeValue());
        } catch (HttpStatusCodeException e) {
            throwError(StringUtils.isBlank(e.getResponseBodyAsString()) ? e.getMessage() : e.getResponseBodyAsString(), "502000", req.getEmail().get(0));
        } catch (Exception e) {
            throwError(e.getMessage(), "502001", req.getEmail().get(0));
        }
    }

    private void throwError(String messageError, String codeError, String referenceId) {
        String error = LogUtils.formatObjectToJson("Send Mail", String.format("API Error: %s", messageError), referenceId);
        log.error(error);
        throw new BadGatewayException(codeError, messageError);
    }

}
