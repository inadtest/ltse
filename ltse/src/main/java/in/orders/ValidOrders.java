package in.orders;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidOrders {
    @Getter
    private final Map<String, List<Order>> orders;

    ValidOrders(Stream<Order> orders) {
        this.orders = orders.filter(Order::isValid).collect(Collectors.toMap(Order::getBroker,
                order -> new ArrayList<>(Collections.singletonList(order)),
                (existing, incoming) -> {
                    existing.addAll(incoming);
                    return existing;
                }));
    }
}
