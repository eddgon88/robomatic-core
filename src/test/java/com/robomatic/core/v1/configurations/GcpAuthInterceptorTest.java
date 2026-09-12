package com.robomatic.core.v1.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GcpAuthInterceptorTest {

    private GcpAuthInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new GcpAuthInterceptor();
    }

    @Test
    @DisplayName("No debe agregar cabecera Authorization si el host no termina en .run.app")
    void shouldNotAddAuthHeaderForNonCloudRunHost() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("http://localhost:5007/test-executor/v1/execute"));

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(any(), any())).thenReturn(response);

        byte[] body = new byte[0];
        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        assertThat(result).isSameAs(response);
        assertThat(headers.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        verify(execution).execute(request, body);
    }

    @Test
    @DisplayName("No debe sobrescribir cabecera Authorization si ya existe una")
    void shouldNotOverwriteExistingAuthHeader() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer my-custom-token");
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("https://robomatic-test-executor-api-80770840815.southamerica-west1.run.app/execute"));

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(any(), any())).thenReturn(response);

        byte[] body = new byte[0];
        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        assertThat(result).isSameAs(response);
        assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer my-custom-token");
        verify(execution).execute(request, body);
    }

    @Test
    @DisplayName("Debe continuar la ejecución sin lanzar excepción si el servidor de metadatos no está disponible")
    void shouldContinueWhenMetadataServerNotReachable() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("https://robomatic-test-executor-api-80770840815.southamerica-west1.run.app/execute"));

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(any(), any())).thenReturn(response);

        byte[] body = new byte[0];
        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        assertThat(result).isSameAs(response);
        verify(execution).execute(request, body);
    }
}
