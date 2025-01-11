package services;

import config.Analyseur;
import utils.FileCounter;
import utils.Jsonfile;
import utils.Readfile;
import services.observer.Observer;
import services.observer.Subject;
import services.IFrequencyAnalyzer;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Classe pour gerer l'analyse de frequence des suites de caracteres.
 * Implemente les patrons de conception: Decorateur, Delegation, Observateur.
 */
public class FrequencyAnalyzer extends AbstractService implements IFrequencyAnalyzer, Subject {

    private DataSource dataSource;
    private Jsonfile<String, Object> jsonfile;
    private ConcurrentMap<String, Map<String, Integer>> allResults = new ConcurrentHashMap<>();
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructeur qui prend en parametre une source de donnees.
     *
     * @param dataSource La source de donnees pour l'analyse.
     */
    public FrequencyAnalyzer(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
        this.jsonfile = new Jsonfile<>();
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
     * Execute l'analyse de frequence.
     *
     * @param nbOccurence Frequence de combien de caracteres (1: monogramme, 2:
     *                    bigramme, 3: trigramme, 4: monogramme + bigramme +
     *                    trigramme).
     */
    @Override
    public void execute(int nbOccurence) {
        // Determiner les occurrences à analyser
        List<Integer> occurrencesToAnalyze = new ArrayList<>();
        if (nbOccurence == 4) {
            occurrencesToAnalyze.add(1);
            occurrencesToAnalyze.add(2);
            occurrencesToAnalyze.add(3);
            System.out.println("Analyse des monogrammes, bigrammes et trigrammes.");
        } else {
            occurrencesToAnalyze.add(nbOccurence);
        }

        for (int occurrence : occurrencesToAnalyze) {
            String texteDir = FileCounter.getTerminalLocation() + "/texte";
            List<String> selectedFiles = dataSource.getFilePaths(texteDir);
            if (selectedFiles.isEmpty()) {
                System.out.println("Aucun fichier selectionne.");
                continue;
            }

            // Preparer une structure pour collecter les resultats de maniere thread-safe
            ConcurrentMap<String, Map<String, Integer>> currentResults = new ConcurrentHashMap<>();

            // Liste des tâches
            List<Callable<Void>> tasks = new ArrayList<>();

            for (String filePath : selectedFiles) {
                Callable<Void> task = () -> {
                    try {
                        // Lire le contenu du fichier
                        String content = Readfile.readFile(filePath);

                        // Analyser la frequence des caracteres
                        Analyseur localAnalyseur = new Analyseur(content, occurrence);
                        localAnalyseur.analyse();
                        Map<String, Integer> frequencyMap = localAnalyseur.getMap();

                        // Stocker le resultat dans la map concurrente
                        String fileName = new File(filePath).getName();
                        currentResults.put(fileName, frequencyMap);

                        System.out.println(
                                "Analyse terminee pour le fichier : " + fileName + " avec occurrence : " + occurrence);

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

                // Afficher les resultats pour cette occurrence
                System.out.println("\nResultats pour occurrence : " + occurrence);
                for (Map.Entry<String, Map<String, Integer>> entry : currentResults.entrySet()) {
                    System.out.println("\nFichier : " + entry.getKey());
                    System.out.println("Frequence des suites de caracteres :");
                    for (Map.Entry<String, Integer> freqEntry : entry.getValue().entrySet()) {
                        System.out.println(freqEntry.getKey() + " : " + freqEntry.getValue());
                    }
                }

                // Generer un fichier JSON contenant tous les resultats
                String jsonOutputPath = FileCounter.getTerminalLocation() + "/resultat/analyseur.json";
                // Merge currentResults into allResults
                for (Map.Entry<String, Map<String, Integer>> entry : currentResults.entrySet()) {
                    allResults.merge(entry.getKey(), entry.getValue(), (oldMap, newMap) -> {
                        for (Map.Entry<String, Integer> freqEntry : newMap.entrySet()) {
                            oldMap.merge(freqEntry.getKey(), freqEntry.getValue(), Integer::sum);
                        }
                        return oldMap;
                    });
                }
                jsonfile.create_json(allResults, jsonOutputPath);
                System.out.println("\nResultats enregistres dans : " + jsonOutputPath);

                // Notifier les observateurs que l'analyse complete est terminee
                notifyObservers("AnalysisCompleted", jsonOutputPath);

            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Erreur lors de l'execution des tâches d'analyse.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Integer> getResults() {
        // Retourne les resultats agreges
        Map<String, Integer> aggregatedResults = new HashMap<>();
        for (Map<String, Integer> freqMap : allResults.values()) {
            for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                aggregatedResults.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return aggregatedResults;
    }
}
