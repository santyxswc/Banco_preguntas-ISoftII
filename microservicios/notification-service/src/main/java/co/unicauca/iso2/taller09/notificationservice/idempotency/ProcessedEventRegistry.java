package co.unicauca.iso2.taller09.notificationservice.idempotency;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessedEventRegistry {

    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    public boolean alreadyProcessed(String eventType, String operationId) {
        return operationId != null && processed.contains(key(eventType, operationId));
    }

    public void markProcessed(String eventType, String operationId) {
        if (operationId != null && !operationId.isBlank()) {
            processed.add(key(eventType, operationId));
        }
    }

    private static String key(String eventType, String operationId) {
        return eventType + ":" + operationId;
    }

    public int size() {
        return processed.size();
    }
}
