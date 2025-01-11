package services;

import java.util.List;

/**
 * Interface pour les sources de donnees.
 */
public interface DataSource {

    /**
     * Recupere les chemins des fichiers à analyser dans un repertoire donne.
     *
     * @param directoryPath Chemin du repertoire contenant les fichiers.
     * @return Liste des chemins des fichiers selectionnes.
     */
    List<String> getFilePaths(String directoryPath);
}
