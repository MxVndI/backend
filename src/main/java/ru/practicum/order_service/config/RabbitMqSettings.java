package ru.practicum.order_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMqSettings {

    private String host;
    private int port;
    private String orderCreatedQueue;
}