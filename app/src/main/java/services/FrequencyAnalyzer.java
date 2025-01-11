package services;

import config.*;
import utils.*;
import services.observer.Observer;
import services.observer.Subject;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Classe pour gerer l'analyse de frequence des suites de caracteres.
 */
public class FrequencyAnalyzer extends AbstractService implements IFrequencyAnalyzer, Subject {

    private DataSource dataSource;
    private Jsonfile<String, Object> jsonfile;
    private ConcurrentMap<String, Map<String, Integer>> allResults = new ConcurrentHashMap<>();
    private List<Observer> observers = new ArrayList<>();

    public FrequencyAnalyzer(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
        this.jsonfile = new Jsonfile<>();
    }

    /**
     * Enregistre un observateur pour les evenements de l'application.
     */
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Supprime un observateur pour les evenements de l'application.
     */
    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifie tous les observateurs d'un evenement.
     */
    @Override
    public void notifyObservers(String eventType, Object data) {
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    /**
     * Execute l'analyse de frequence des suites de caracteres.
     */
    @Override
    public void execute(int nbOccurence) {
        String texteDir = FileCounter.getTerminalLocation() + "/texte";
        List<String> selectedFiles = dataSource.getFilePaths(texteDir);
        if (selectedFiles.isEmpty()) {
            System.out.println("Aucun fichier selectionne.");
            return;
        }

        // Liste des tâches
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String filePath : selectedFiles) {
            Callable<Void> task = () -> {
                try {
                    // Lire le contenu du fichier
                    String content = Readfile.readFile(filePath);

                    // Analyser la frequence des caracteres
                    Analyseur localAnalyseur = new Analyseur(content, nbOccurence);
                    localAnalyseur.analyse();
                    Map<String, Integer> frequencyMap = localAnalyseur.getMap();

                    // Stocker le resultat dans la map concurrente
                    String fileName = new File(filePath).getName();
                    allResults.put(fileName, frequencyMap);

                    System.out.println("Analyse terminee pour le fichier : " + fileName);

                    // Notifier les observateurs pour chaque fichier analyse
                    notifyObservers("FileAnalyzed", fileName);

                } catch (Exception e) {
                    System.out.println("Erreur lors de l'analyse du fichier : " + filePath);
                    e.printStackTrace();
                }
                return null;
            };
            tasks.add(task);
        }

        try {
            // Executer toutes les tâches
            List<Future<Void>> futures = executor.invokeAll(tasks);

            // Attendre que toutes les tâches soient terminees
            for (Future<Void> future : futures) {
                future.get(); // Cela peut lancer des exceptions si les tâches echouent
            }

            // Afficher les resultats
            for (Map.Entry<String, Map<String, Integer>> entry : allResults.entrySet()) {
                System.out.println("\nFichier : " + entry.getKey());
                System.out.println("Frequence des suites de caracteres :");
                for (Map.Entry<String, Integer> freqEntry : entry.getValue().entrySet()) {
                    System.out.println(freqEntry.getKey() + " : " + freqEntry.getValue());
                }
            }

            // Generer un fichier JSON contenant tous les resultats
            String jsonOutputPath = FileCounter.getTerminalLocation() + "/resultat/analyseur.json";
            jsonfile.create_json(allResults, jsonOutputPath);

            // Notifier les observateurs que l'analyse complete est terminee
            notifyObservers("AnalysisCompleted", jsonOutputPath);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Erreur lors de l'execution des tâches d'analyse.");
            e.printStackTrace();
        }
    }

    /**
     * Retourne les resultats de l'analyse de frequence.
     */
    @Override
    public Map<String, Integer> getResults() {
        Map<String, Integer> aggregatedResults = new HashMap<>();
        for (Map<String, Integer> freqMap : allResults.values()) {
            for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                aggregatedResults.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return aggregatedResults;
    }
}
