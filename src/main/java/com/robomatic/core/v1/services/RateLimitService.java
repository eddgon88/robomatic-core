package com.robomatic.core.v1.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de Rate Limiting para prevenir ataques de fuerza bruta en login
 * Limita el número de intentos de login por IP
 */
@Service
public class RateLimitService {

    private static final double REQUESTS_PER_MINUTE = 1.0;
    private static final int WINDOW_MINUTES = 15;

    private final LoadingCache<String, RateLimiter> limiters;

    public RateLimitService() {
        this.limiters = CacheBuilder.newBuilder()
                .expireAfterAccess(WINDOW_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, RateLimiter>() {
                    @Override
                    public RateLimiter load(String key) {
                        return RateLimiter.create(REQUESTS_PER_MINUTE);
                    }
                });
    }

    /**
     * Verifica si la IP ha excedido el límite de intentos de login
     * @param clientIp dirección IP del cliente
     * @return true si se permite el intento, false si está limitado
     */
    public boolean isAllowed(String clientIp) {
        try {
            RateLimiter rateLimiter = limiters.get(clientIp);
            return rateLimiter.tryAcquire();
        } catch (ExecutionException e) {
            return true;
        }
    }
}
