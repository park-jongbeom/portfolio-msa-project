package com.qk54r71.apigatewayservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    @Primary
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        // Key는 String, Value도 JSON String으로 저장하기 위해 String 직렬화 사용
        val serializer = StringRedisSerializer()
        val builder = RedisSerializationContext.newSerializationContext<String, String>(serializer)
        val context = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}