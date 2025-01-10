package services;

import config.Analyseur;
import utils.FileCounter;
import utils.Jsonfile;
import utils.Readfile;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Classe pour gerer l'analyse de frequence des suites de caracteres.
 */
public class FrequencyAnalyzer extends AbstractService {

    private FileCounter fileCounter;
    private Jsonfile<String, Object> jsonfile; // Utilisation de Object pour permettre Map<String, Map<String, Integer>>

    public FrequencyAnalyzer() {
        super();
        this.fileCounter = new FileCounter();
        this.jsonfile = new Jsonfile<>();
    }

    /**
     * Execute l'analyse de frequence.
     *
     * @param nb_occurence Frequence de combien de caracteres (nb_occurence).
     */
    public void execute(int nb_occurence) {
        String texteDir = FileCounter.getTerminalLocation() + "/texte";
        List<String> selectedFiles = fileCounter.selectFiles(texteDir);
        if (selectedFiles.isEmpty()) {
            System.out.println("Aucun fichier selectionne.");
            return;
        }

        // Preparer une structure pour collecter les resultats de maniere thread-safe
        ConcurrentMap<String, Map<String, Integer>> allResults = new ConcurrentHashMap<>();

        // Liste des tâches
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String filePath : selectedFiles) {
            Callable<Void> task = () -> {
                try {
                    // Lire le contenu du fichier
                    String content = Readfile.readFile(filePath);

                    // Analyser la frequence des caracteres
                    Analyseur localAnalyseur = new Analyseur(content, nb_occurence);
                    localAnalyseur.analyse();
                    Map<String, Integer> frequencyMap = localAnalyseur.getMap();

                    // Stocker le resultat dans la map concurrente
                    String fileName = new File(filePath).getName();
                    allResults.put(fileName, frequencyMap);

                    System.out.println("Analyse terminee pour le fichier : " + fileName);
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
            System.out.println("\nResultats enregistres dans : " + jsonOutputPath);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Erreur lors de l'execution des tâches d'analyse.");
            e.printStackTrace();
        }
    }
}
