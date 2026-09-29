package com.example.productservice.config;

import com.example.productservice.service.PromotionUpdateSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisSubscriberConfig {

    @Bean
    public MessageListenerAdapter messageListenerAdapter(
            PromotionUpdateSubscriber subscriber) {

        return new MessageListenerAdapter(
                subscriber,
                "handleMessage"
        );
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                listenerAdapter,
                new PatternTopic("promotion-updates")
        );

        return container;
    }
}