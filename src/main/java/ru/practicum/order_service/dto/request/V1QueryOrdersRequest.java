package ru.practicum.order_service.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class V1QueryOrdersRequest {
    private List<Long> ids;
    private List<Long> customerIds;

    @PositiveOrZero(message = "Page must be zero or positive")
    private Integer page = 0;

    @Positive(message = "Page size must be positive")
    private Integer pageSize = 20;

    private Boolean includeOrderItems = false;

}