package com.quevedo.mercado.inventario.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configuracion del CacheManager basado en Redis.
 *
 * Se usa GenericJackson2JsonRedisSerializer (en vez del serializador JDK
 * por defecto) para que los valores queden almacenados en Redis como JSON
 * legible en lugar de bytes serializados de Java, y para poder deserializar
 * correctamente tipos genericos como PageResultDTO<ProductoResponseDTO>.
 *
 * El TTL (time-to-live) de las entradas del cache "productos" se define en
 * application.yml (spring.cache.redis.time-to-live) y se refuerza aqui
 * explicitamente para el cache "productos".
 */
@Configuration
public class RedisCacheConfig {

    @Value("${spring.cache.redis.time-to-live:600000}")
    private long cacheTtlMs;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(cacheTtlMs))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration cacheConfiguration) {
        return builder -> builder
                .cacheDefaults(cacheConfiguration)
                .withCacheConfiguration("productos", cacheConfiguration);
    }

}
