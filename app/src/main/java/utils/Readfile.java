package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utilitaire pour la lecture de fichiers.
 */
public class Readfile {

    /**
     * Lit le contenu d'un fichier et le retourne sous forme de chaîne de
     * caracteres.
     *
     * @param filePath Chemin du fichier à lire.
     * @return Contenu du fichier en tant que String.
     */
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier : " + filePath);
            e.printStackTrace();
        }
        return content.toString();
    }
}
