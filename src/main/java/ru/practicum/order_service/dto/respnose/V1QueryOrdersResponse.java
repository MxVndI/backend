package ru.practicum.order_service.dto.respnose;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.order_service.dto.common.OrderUnit;

import java.util.List;

@Setter
@Getter
public class V1QueryOrdersResponse {

    private List<OrderUnit> orders;
    public V1QueryOrdersResponse(List<OrderUnit> orders) {
        this.orders = orders;
    }
}