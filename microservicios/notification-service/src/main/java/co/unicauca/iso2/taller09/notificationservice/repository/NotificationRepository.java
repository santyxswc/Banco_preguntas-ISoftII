package co.unicauca.iso2.taller09.notificationservice.repository;

import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NotificationRepository {
    private final Map<Long, Notification> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idGen.incrementAndGet());
        }
        storage.put(notification.getId(), notification);
        return notification;
    }

    public List<Notification> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Notification> findByRecipientId(Long recipientId) {
        return storage.values().stream()
                .filter(n -> recipientId.equals(n.getRecipientId()))
                .toList();
    }
}
