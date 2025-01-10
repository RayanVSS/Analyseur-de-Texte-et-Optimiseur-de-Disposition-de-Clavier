package services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe abstraite pour gerer le cycle de vie du ExecutorService.
 */
public abstract class AbstractService {
    protected ExecutorService executor;

    public AbstractService() {
        // Initialiser les threads avec le nombre de processeurs disponibles
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Ferme proprement le ExecutorService.
     */
    public void shutdownExecutor() {
        try {
            System.out.println("Tentative d'arret du pool de threads...");
            Thread.sleep(1000);
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("Forçage de l'arret du pool de threads...");
                executor.shutdownNow();
            }
            System.out.println("Pool de threads arrete.");
        } catch (InterruptedException e) {
            System.out.println("Erreur lors de l'arret du pool de threads.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
