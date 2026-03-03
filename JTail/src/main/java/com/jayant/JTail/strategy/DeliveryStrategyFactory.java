package com.jayant.JTail.strategy;

import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DeliveryStrategyFactory {

    private final Map<DeliverySpeed, DeliveryStrategy> strategyMap;

    public DeliveryStrategyFactory(List<DeliveryStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        DeliveryStrategy::getDeliverySpeed,
                        Function.identity()
                ));
    }

    public DeliveryStrategy getStrategy(DeliverySpeed speed) {
        DeliveryStrategy strategy = strategyMap.get(speed);
        if (strategy == null) {
            throw new InvalidRequestException(
                    "Unsupported delivery speed: " + speed +
                    ". Supported: " + strategyMap.keySet()
            );
        }
        return strategy;
    }
}