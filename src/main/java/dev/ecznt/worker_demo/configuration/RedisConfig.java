package dev.ecznt.worker_demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * This method creates and configures a RedisTemplate bean. Spring Boot does not
     * provide this specific generic bean by default, so we must define it.
     * This bean is essential for serializing and deserializing Java objects to and from Redis.
     * @param connectionFactory The auto-configured Redis connection factory.
     * @return A configured RedisTemplate instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys to ensure they are human-readable in Redis.
        template.setKeySerializer(new StringRedisSerializer());

        // Use GenericJackson2JsonRedisSerializer for values to store complex Java objects as JSON.
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}