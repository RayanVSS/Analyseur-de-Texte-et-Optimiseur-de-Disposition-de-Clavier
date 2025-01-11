package services.observer;

/**
 * Interface pour les sujets observables.
 */
public interface Subject {
    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
    void notifyObservers(String eventType, Object data);
}