package services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import utils.FileCounter;

/**
 * Classe pour vider les resultats de l'analyseur (le fichier JSON).
 */
public class ResultClearer {

    /**
     * Vider le contenu du fichier JSON des resultats d'analyse.
     */
    public void execute() {
        String resultFilePath = FileCounter.getTerminalLocation() + "/resultat/analyseur.json";
        File resultFile = new File(resultFilePath);

        if (!resultFile.exists()) {
            System.out.println("Le fichier des resultats d'analyse n'existe pas.");
            return;
        }

        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write("{}"); // Ecrire un objet JSON vide
            System.out.println("Les resultats de l'analyseur ont ete vides avec succes.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la vidange du fichier JSON des resultats.");
            e.printStackTrace();
        }
    }
}
