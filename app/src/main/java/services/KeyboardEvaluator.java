package services;

import config.Evaluateur;
import utils.Jsonfile;
import utils.FileCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour gerer l'evaluation de la disposition du clavier.
 */
public class KeyboardEvaluator extends AbstractService {

    private Jsonfile<String, Integer> jsonfile;
    private FileCounter fileCounter;

    public KeyboardEvaluator() {
        super();
        this.jsonfile = new Jsonfile<>();
        this.fileCounter = new FileCounter();
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

            // Charger la disposition du clavier selectionnee
            HashMap<Character, Evaluateur.TouchInfo> dispoMap = Jsonfile.loadDispositionFromJson(dispositionPath);
            if (dispoMap == null || dispoMap.isEmpty()) {
                System.out.println("La disposition du clavier est vide ou le fichier n'existe pas.");
                return;
            }

            // Creer l'evaluateur avec les stats agregees et la disposition chargee
            Evaluateur evaluateur = new Evaluateur(aggregatedStats, dispoMap);
            evaluateur.evaluer();
            evaluateur.afficherScores();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement ou de l'agregation des statistiques.");
            e.printStackTrace();
        }
    }
}
