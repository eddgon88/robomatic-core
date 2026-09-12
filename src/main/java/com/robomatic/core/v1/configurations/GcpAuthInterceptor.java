package com.robomatic.core.v1.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptor para RestTemplate que inyecta automáticamente un Google Cloud OIDC ID Token
 * al invocar servicios internos desplegados en Cloud Run (*.run.app).
 *
 * Mantiene un caché en memoria de 50 minutos para minimizar consultas al servidor de metadatos de GCP.
 * En entornos locales (desarrollo/Docker Compose), si el host no es Cloud Run o no hay servidor de metadatos,
 * continúa la petición sin alterar las cabeceras.
 */
@Slf4j
@Component
public class GcpAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String METADATA_IDENTITY_URL =
            "http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/identity?audience=";
    private static final long TOKEN_CACHE_DURATION_MS = 50 * 60 * 1000L; // 50 minutos (los tokens OIDC expiran a los 60 min)

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private final Map<String, CachedToken> tokenCache = new ConcurrentHashMap<>();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        URI uri = request.getURI();
        String host = uri.getHost();

        // Solo inyectar en destinos Cloud Run (*.run.app) que no tengan ya cabecera Authorization
        if (host != null && host.endsWith(".run.app") && !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            String audience = uri.getScheme() + "://" + host;
            String idToken = getIdTokenForAudience(audience);
            if (idToken != null && !idToken.isBlank()) {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + idToken);
                log.debug("GCP ID Token adjuntado exitosamente para audience: {}", audience);
            }
        }

        return execution.execute(request, body);
    }

    private String getIdTokenForAudience(String audience) {
        long now = System.currentTimeMillis();
        CachedToken cached = tokenCache.get(audience);
        if (cached != null && cached.expiryTimeMillis > now) {
            return cached.token;
        }

        synchronized (this) {
            cached = tokenCache.get(audience);
            if (cached != null && cached.expiryTimeMillis > now) {
                return cached.token;
            }

            try {
                String encodedAudience = URLEncoder.encode(audience, StandardCharsets.UTF_8);
                java.net.http.HttpRequest gcpReq = java.net.http.HttpRequest.newBuilder()
                        .uri(URI.create(METADATA_IDENTITY_URL + encodedAudience))
                        .header("Metadata-Flavor", "Google")
                        .timeout(Duration.ofSeconds(3))
                        .GET()
                        .build();

                HttpResponse<String> gcpRes = httpClient.send(gcpReq, HttpResponse.BodyHandlers.ofString());

                if (gcpRes.statusCode() == 200 && gcpRes.body() != null && !gcpRes.body().isBlank()) {
                    String token = gcpRes.body().trim();
                    tokenCache.put(audience, new CachedToken(token, now + TOKEN_CACHE_DURATION_MS));
                    log.info("Obtenido nuevo GCP ID Token desde servidor de metadatos para audience: {}", audience);
                    return token;
                } else {
                    log.warn("Respuesta no exitosa ({}) del servidor de metadatos para audience: {}", gcpRes.statusCode(), audience);
                }
            } catch (Exception e) {
                log.debug("No se pudo obtener GCP ID Token desde el servidor de metadatos para {}: {}", audience, e.getMessage());
            }
        }
        return null;
    }

    private static class CachedToken {
        final String token;
        final long expiryTimeMillis;

        CachedToken(String token, long expiryTimeMillis) {
            this.token = token;
            this.expiryTimeMillis = expiryTimeMillis;
        }
    }
}
