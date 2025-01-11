package services.observer;

import java.util.EventListener;

// Interface Observateur
public interface Observer extends EventListener {
    void update(String eventType, Object data);
}

