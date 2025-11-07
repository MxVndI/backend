package ru.practicum.order_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.order_service.consumer.OrderCreatedConsumer;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqSettings settings;

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(settings.getOrderCreatedQueue(), false);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory,
                                                            OrderCreatedConsumer consumer) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(orderCreatedQueue());
        container.setMessageListener(consumer);
        return container;
    }

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(settings.getHost());
        factory.setPort(settings.getPort());
        return factory;
    }
}
