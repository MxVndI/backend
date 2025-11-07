package ru.practicum.order_service.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.order_service.common.JsonUtil;
import ru.practicum.order_service.config.RabbitMqSettings;
import ru.practicum.order_service.dto.OrderCreatedMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RabbitMqPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqSettings settings;

    public void publish(List<OrderCreatedMessage> messages, String queue) {
        for (Object msg : messages) {
            String json = JsonUtil.toJson(msg);
            rabbitTemplate.convertAndSend(queue, json);
        }
    }

    public void publishOrderCreated(List<OrderCreatedMessage> messages) {
        publish(messages, settings.getOrderCreatedQueue());
    }
}
