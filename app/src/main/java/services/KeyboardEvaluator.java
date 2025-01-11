package services;

import builders.KeyboardLayoutBuilder;
import builders.JsonKeyboardLayoutBuilder;
import builders.KeyboardLayoutDirector;
import config.Evaluateur;
import models.KeyboardLayout;
import utils.Jsonfile;
import utils.FileCounter;
import services.observer.Observer;
import services.observer.Subject;

import java.util.*;

/**
 * Classe pour gerer l'evaluation de la disposition du clavier.
 * Implemente le patron Observateur/Observable.
 */
public class KeyboardEvaluator extends AbstractService implements Subject {

    private Jsonfile<String, Integer> jsonfile;
    private FileCounter fileCounter;
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructeur qui initialise les utilitaires necessaires.
     */
    public KeyboardEvaluator() {
        super();
        this.jsonfile = new Jsonfile<>();
        this.fileCounter = new FileCounter();
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
     * Execute l'evaluation de la disposition du clavier.
     */
    public void execute() {
        String resultFile = FileCounter.getTerminalLocation() + "/resultat/analyseur.json";

        // Charger les statistiques d'occurrences
        HashMap<String, Integer> aggregatedStats = new HashMap<>();

        // Charger chaque fichier d'analyse et agreger les statistiques
        try {
            Map<String, Map<String, Integer>> allAnalysis = jsonfile.readJsonAsMapStringMapStringInteger(resultFile);
            if (allAnalysis == null || allAnalysis.isEmpty()) {
                System.out.println("Les statistiques d'occurrences sont vides ou le fichier n'existe pas.");
                return;
            }

            // Agreger les statistiques de tous les fichiers
            for (Map<String, Integer> freqMap : allAnalysis.values()) {
                for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                    aggregatedStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }

            // Selectionner la disposition du clavier
            String dispositionPath = fileCounter.selectKeyboardLayout();
            if (dispositionPath == null)
                return;

            // Utiliser le Builder pour construire le KeyboardLayout
            KeyboardLayoutDirector director = new KeyboardLayoutDirector();
            KeyboardLayoutBuilder builder = new JsonKeyboardLayoutBuilder(dispositionPath);
            director.setBuilder(builder);
            KeyboardLayout keyboardLayout = director.constructKeyboardLayout();

            if (keyboardLayout.getDisposition().isEmpty()) {
                System.out.println("La disposition du clavier est vide ou le fichier n'existe pas.");
                return;
            }

            // Creer l'evaluateur avec les stats agregees et la disposition chargee
            Evaluateur evaluateur = new Evaluateur(aggregatedStats, keyboardLayout.getDisposition());
            evaluateur.evaluer();
            evaluateur.afficherScores();

            // Notifier les observateurs que l'evaluation est terminee
            notifyObservers("KeyboardEvaluated", dispositionPath);

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement ou de l'agregation des statistiques.");
            e.printStackTrace();
        }
    }
}
