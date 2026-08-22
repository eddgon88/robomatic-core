package com.robomatic.core.v1.services;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class R2StorageService {

    @Value("${r2.endpoint:${R2_ENDPOINT:}}")
    private String endpoint;

    @Value("${r2.accessKeyId:${R2_ACCESS_KEY_ID:}}")
    private String accessKeyId;

    @Value("${r2.secretAccessKey:${R2_SECRET_ACCESS_KEY:}}")
    private String secretAccessKey;

    @Value("${r2.bucketName:${R2_BUCKET_NAME:robomatic-evidence}}")
    private String bucketName;

    private AmazonS3 s3Client;
    private boolean enabled = false;

    @PostConstruct
    public void init() {
        if (endpoint != null && !endpoint.trim().isEmpty() &&
            accessKeyId != null && !accessKeyId.trim().isEmpty() &&
            secretAccessKey != null && !secretAccessKey.trim().isEmpty()) {
            try {
                AWSCredentials credentials = new BasicAWSCredentials(accessKeyId.trim(), secretAccessKey.trim());
                ClientConfiguration clientConfig = new ClientConfiguration();
                clientConfig.setProtocol(Protocol.HTTPS);

                this.s3Client = AmazonS3ClientBuilder.standard()
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint.trim(), "auto"))
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withClientConfiguration(clientConfig)
                        .withPathStyleAccessEnabled(true)
                        .build();

                this.enabled = true;
                log.info("R2StorageService successfully connected to Cloudflare R2 bucket: {}", bucketName);
            } catch (Exception e) {
                log.error("Failed to initialize R2StorageService for Cloudflare R2: {}", e.getMessage(), e);
                this.enabled = false;
            }
        } else {
            log.info("Cloudflare R2 credentials not fully configured. Using local file storage fallback.");
        }
    }

    public boolean isEnabled() {
        return this.enabled && this.s3Client != null;
    }

    /**
     * Sube un archivo de caso de prueba (.csv) a Cloudflare R2 bajo el prefijo 'cases/'.
     */
    public void uploadTestCase(String fileDirOrName, String contentBase64OrPlain) {
        if (!isEnabled()) {
            return;
        }
        try {
            String fileName = new File(fileDirOrName).getName();
            String r2Key = "cases/" + fileName;

            byte[] contentBytes;
            try {
                contentBytes = Base64.getDecoder().decode(contentBase64OrPlain);
            } catch (IllegalArgumentException e) {
                contentBytes = contentBase64OrPlain.getBytes(StandardCharsets.UTF_8);
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentBytes.length);
            metadata.setContentType("text/csv");

            s3Client.putObject(bucketName, r2Key, new ByteArrayInputStream(contentBytes), metadata);
            // También subir con la clave simple como compatibilidad
            s3Client.putObject(bucketName, fileName, new ByteArrayInputStream(contentBytes), metadata);

            log.info("Test case file uploaded to Cloudflare R2: {}", r2Key);
        } catch (Exception e) {
            log.error("Error uploading test case to Cloudflare R2: {}", e.getMessage(), e);
        }
    }

    /**
     * Descarga y obtiene el contenido en Base64 de un caso de prueba desde Cloudflare R2.
     * Retorna null si no se encuentra en R2.
     */
    public String getTestCaseBase64(String fileDirOrName) {
        if (!isEnabled()) {
            return null;
        }
        try {
            String fileName = new File(fileDirOrName).getName();
            String[] candidateKeys = new String[]{"cases/" + fileName, fileName};

            for (String key : candidateKeys) {
                if (s3Client.doesObjectExist(bucketName, key)) {
                    S3Object object = s3Client.getObject(bucketName, key);
                    try (S3ObjectInputStream stream = object.getObjectContent()) {
                        byte[] bytes = IOUtils.toByteArray(stream);
                        log.info("Test case file read from Cloudflare R2: {}", key);
                        return Base64.getEncoder().encodeToString(bytes);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error reading test case from Cloudflare R2 for {}: {}", fileDirOrName, e.getMessage());
        }
        return null;
    }
}
