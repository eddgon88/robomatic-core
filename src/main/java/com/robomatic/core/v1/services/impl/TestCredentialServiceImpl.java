package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.dtos.ConstantsDto;
import com.robomatic.core.v1.entities.TestCredentialEntity;
import com.robomatic.core.v1.enums.CredentialTypeEnum;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.ForbiddenException;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.models.CreateCredentialRequestModel;
import com.robomatic.core.v1.models.CredentialExecutionModel;
import com.robomatic.core.v1.models.CredentialModel;
import com.robomatic.core.v1.models.UpdateCredentialRequestModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRepository;
import com.robomatic.core.v1.repositories.TestCredentialRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.CredentialEncryptionService;
import com.robomatic.core.v1.services.TestCredentialService;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.robomatic.core.v1.exceptions.messages.ForbiddenErrorCode.E403001;

@Slf4j
@Service
public class TestCredentialServiceImpl implements TestCredentialService {

    @Autowired
    private TestCredentialRepository credentialRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private CredentialEncryptionService encryptionService;

    @Autowired
    private ConstantsDto constantsDto;

    @Autowired
    private UserModel currentUser;

    /**
     * Valida que el usuario pueda gestionar credenciales de un test.
     * Un usuario puede gestionar credenciales si:
     * - Tiene rol ADMIN o ANALYST
     * - Es el owner del test
     * - Tiene permiso de edición (EDIT_PERMISSION)
     * - Tiene permiso de ejecución (EXECUTE_PERMISSION)
     */
    private void validateUserCanManageCredentials(Integer testId) {
        Integer roleId = currentUser.getRoleId();
        Integer userId = currentUser.getId();

        // Los ADMIN y ANALYST pueden gestionar cualquier credencial
        if (roleId != null && 
            (roleId.equals(RoleEnum.ADMIN.getCode()) || roleId.equals(RoleEnum.ANALYST.getCode()))) {
            return;
        }

        // Verificar si el usuario tiene permisos sobre este test específico
        // (es owner, tiene permiso de edición o de ejecución)
        if (actionRepository.canUserModifyTest(testId, userId)) {
            return;
        }

        throw new ForbiddenException(E403001, "You don't have permission to manage credentials for this test");
    }

    @Override
    @Transactional
    public TestCredentialEntity createCredential(CreateCredentialRequestModel request) {
        // Validar que el test existe
        testRepository.findById(request.getTestId())
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404002));

        // Validar que el usuario puede gestionar credenciales
        validateUserCanManageCredentials(request.getTestId());

        // Validar que no exista una credencial con el mismo nombre para el test
        credentialRepository.findByTestIdAndName(request.getTestId(), request.getName())
                .ifPresent(c -> {
                    throw new BadRequestException(BadRequestErrorCode.E400001, 
                            "A credential with this name already exists for this test");
                });

        String credentialId = RobomaticStringUtils.createRandomId(constantsDto.getCredentialPrefix());
        LocalDateTime now = LocalDateTime.now();

        TestCredentialEntity entity = TestCredentialEntity.builder()
                .credentialId(credentialId)
                .testId(request.getTestId())
                .credentialTypeId(request.getCredentialTypeId())
                .name(request.getName())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Procesar según el tipo de credencial
        if (request.getCredentialTypeId().equals(CredentialTypeEnum.PASSWORD.getCode())) {
            // Encriptar el password
            if (request.getValue() == null || request.getValue().isEmpty()) {
                throw new BadRequestException(BadRequestErrorCode.E400001, "Password value is required");
            }
            String encryptedValue = encryptionService.encrypt(request.getValue());
            entity.setEncryptedValue(encryptedValue);

        } else if (request.getCredentialTypeId().equals(CredentialTypeEnum.CERTIFICATE.getCode())) {
            // Guardar el archivo de certificado
            if (request.getFileContent() == null || request.getFileName() == null) {
                throw new BadRequestException(BadRequestErrorCode.E400001, "File content and name are required for certificates");
            }
            String filePath = saveCertificateFile(request.getTestId(), credentialId, 
                    request.getFileName(), request.getFileContent());
            entity.setFilePath(filePath);
            entity.setFileName(request.getFileName());
        }

        return credentialRepository.save(entity);
    }

    @Override
    @Transactional
    public TestCredentialEntity updateCredential(UpdateCredentialRequestModel request) {
        TestCredentialEntity entity = credentialRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404012));

        // Validar que el usuario puede gestionar credenciales
        validateUserCanManageCredentials(entity.getTestId());

        // Actualizar nombre si se proporciona
        if (request.getName() != null && !request.getName().isEmpty()) {
            // Validar que no exista otra credencial con el mismo nombre
            credentialRepository.findByTestIdAndName(entity.getTestId(), request.getName())
                    .ifPresent(c -> {
                        if (!c.getId().equals(entity.getId())) {
                            throw new BadRequestException(BadRequestErrorCode.E400001, 
                                    "A credential with this name already exists for this test");
                        }
                    });
            entity.setName(request.getName());
        }

        // Actualizar valor si se proporciona
        if (request.getValue() != null && !request.getValue().isEmpty()) {
            if (entity.getCredentialTypeId().equals(CredentialTypeEnum.PASSWORD.getCode())) {
                String encryptedValue = encryptionService.encrypt(request.getValue());
                entity.setEncryptedValue(encryptedValue);
            }
        }

        // Actualizar archivo si se proporciona
        if (request.getFileContent() != null && request.getFileName() != null) {
            if (entity.getCredentialTypeId().equals(CredentialTypeEnum.CERTIFICATE.getCode())) {
                // Eliminar archivo anterior si existe
                if (entity.getFilePath() != null) {
                    deleteCertificateFile(entity.getFilePath());
                }
                String filePath = saveCertificateFile(entity.getTestId(), entity.getCredentialId(),
                        request.getFileName(), request.getFileContent());
                entity.setFilePath(filePath);
                entity.setFileName(request.getFileName());
            }
        }

        entity.setUpdatedAt(LocalDateTime.now());
        return credentialRepository.save(entity);
    }

    @Override
    public List<CredentialModel> getCredentialsByTestId(Integer testId) {
        List<TestCredentialEntity> entities = credentialRepository.findByTestId(testId);
        
        return entities.stream()
                .map(this::mapToCredentialModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCredential(Long credentialId) {
        TestCredentialEntity entity = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404012));

        // Validar que el usuario puede gestionar credenciales
        validateUserCanManageCredentials(entity.getTestId());

        // Eliminar archivo de certificado si existe
        if (entity.getFilePath() != null) {
            deleteCertificateFile(entity.getFilePath());
        }

        credentialRepository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteCredentialsByTestId(Integer testId) {
        List<TestCredentialEntity> entities = credentialRepository.findByTestId(testId);
        
        // Eliminar archivos de certificados
        for (TestCredentialEntity entity : entities) {
            if (entity.getFilePath() != null) {
                deleteCertificateFile(entity.getFilePath());
            }
        }

        credentialRepository.deleteByTestId(testId);
    }

    @Override
    public List<CredentialExecutionModel> getCredentialsForExecution(Integer testId) {
        List<TestCredentialEntity> entities = credentialRepository.findByTestId(testId);
        
        return entities.stream()
                .map(entity -> CredentialExecutionModel.builder()
                        .name(entity.getName())
                        .credentialTypeId(entity.getCredentialTypeId())
                        .encryptedValue(entity.getEncryptedValue())
                        .filePath(entity.getFilePath())
                        .build())
                .collect(Collectors.toList());
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private CredentialModel mapToCredentialModel(TestCredentialEntity entity) {
        return CredentialModel.builder()
                .id(entity.getId())
                .credentialId(entity.getCredentialId())
                .testId(entity.getTestId())
                .credentialTypeId(entity.getCredentialTypeId())
                .credentialTypeName(CredentialTypeEnum.fromCode(entity.getCredentialTypeId()).getName())
                .name(entity.getName())
                .fileName(entity.getFileName())
                .hasValue(entity.getEncryptedValue() != null || entity.getFilePath() != null)
                .build();
    }

    private String saveCertificateFile(Integer testId, String credentialId, String fileName, String fileContentBase64) {
        try {
            // Crear directorio para credenciales del test
            Path testCredentialsDir = Paths.get(constantsDto.getCredentialFileDir(), testId.toString());
            if (!Files.exists(testCredentialsDir)) {
                Files.createDirectories(testCredentialsDir);
            }

            // Generar nombre único para el archivo
            String uniqueFileName = credentialId + "_" + fileName;
            Path filePath = testCredentialsDir.resolve(uniqueFileName);

            // Decodificar Base64 y guardar
            byte[] fileContent = Base64.getDecoder().decode(fileContentBase64);
            Files.write(filePath, fileContent);

            log.info("Certificate file saved: {}", filePath);
            return filePath.toString();

        } catch (IOException e) {
            log.error("Error saving certificate file: {}", e.getMessage());
            throw new InternalErrorException(InternalErrorCode.E500000, "Error saving certificate file");
        }
    }

    private void deleteCertificateFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Certificate file deleted: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Error deleting certificate file: {}", e.getMessage());
        }
    }

}


