package services;

import utils.Jsonfile;
import utils.FileCounter;
import services.observer.Observer;
import services.observer.Subject;
import config.Optimisateur;
import java.util.*;

/**
 * Classe pour gerer l'evaluation de la disposition du clavier.
 * Implemente le patron Observateur/Observable.
 */
public class KeyboardOptimisateur extends AbstractService implements Subject {
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructeur qui initialise les utilitaires necessaires.
     */
    public KeyboardOptimisateur() {
        super();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    /**
     * Execute l'optimisation de la disposition du clavier.
     */
    public void execute() {
        try {
            String resultFile = FileCounter.getTerminalLocation() + "/resultat/analyseur.json";

            // Charger les statistiques d'occurrences
            HashMap<String, Integer> aggregatedStats = new HashMap<>();

            // Charger chaque fichier d'analyse et agreger les statistiques
            Map<String, Map<String, Integer>> allAnalysis = Jsonfile.readJsonAsMapStringMapStringInteger(resultFile);
            if (allAnalysis == null || allAnalysis.isEmpty()) {
                notifyObservers("StatisticsNotFound", null);
            }

            // Agreger les statistiques de tous les fichiers
            for (Map<String, Integer> freqMap : allAnalysis.values()) {
                for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                    aggregatedStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }

            if (!aggregatedStats.isEmpty()) {
                notifyObservers("StatisticsLoaded", null);
                // Creer un optimisateur
                Optimisateur optimisateur = new Optimisateur(aggregatedStats);
                optimisateur.optimiser();
                optimisateur.sauvegarderDisposition();

            } else {
                notifyObservers("StatisticsNotFound", null);
                return;
            }
            notifyObservers("OptimizationKeyBoard", null);
       
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement ou de l'agregation des statistiques.");
            e.printStackTrace();
        }
    }
}
