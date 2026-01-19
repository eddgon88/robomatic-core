package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.TestCredentialEntity;
import com.robomatic.core.v1.models.CreateCredentialRequestModel;
import com.robomatic.core.v1.models.CredentialExecutionModel;
import com.robomatic.core.v1.models.CredentialModel;
import com.robomatic.core.v1.models.UpdateCredentialRequestModel;

import java.util.List;

public interface TestCredentialService {

    /**
     * Crea una nueva credencial para un test
     */
    TestCredentialEntity createCredential(CreateCredentialRequestModel request);

    /**
     * Actualiza una credencial existente
     */
    TestCredentialEntity updateCredential(UpdateCredentialRequestModel request);

    /**
     * Obtiene todas las credenciales de un test (sin valores sensibles)
     */
    List<CredentialModel> getCredentialsByTestId(Integer testId);

    /**
     * Elimina una credencial por su ID
     */
    void deleteCredential(Long credentialId);

    /**
     * Elimina todas las credenciales de un test
     */
    void deleteCredentialsByTestId(Integer testId);

    /**
     * Obtiene las credenciales para ejecución (con valores encriptados)
     */
    List<CredentialExecutionModel> getCredentialsForExecution(Integer testId);

}


