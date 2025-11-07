package ru.practicum.order_service.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;
import ru.practicum.order_service.audit.OmsClient;
import ru.practicum.order_service.common.JsonUtil;
import ru.practicum.order_service.dto.OrderCreatedMessage;
import ru.practicum.order_service.dto.request.V1AuditLogOrderRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer implements MessageListener {

    private final OmsClient omsClient;

    @Override
    public void onMessage(Message message) {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        OrderCreatedMessage order = JsonUtil.fromJson(json, OrderCreatedMessage.class);

        // асинхронная запись в БД
        List<V1AuditLogOrderRequest.LogOrder> logs = order.getOrderItems().stream()
                .map(item -> new V1AuditLogOrderRequest.LogOrder(
                        order.getId(),
                        item.getId(),
                        order.getCustomerId(),
                        "CREATED"
                ))
                .toList();

        V1AuditLogOrderRequest request = new V1AuditLogOrderRequest();
        request.setOrders(logs);
        omsClient.logOrder(request);
    }
}
