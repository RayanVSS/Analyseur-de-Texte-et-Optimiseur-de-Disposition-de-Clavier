package services;

import java.util.List;

/**
 * Interface pour les sources de données.
 */
public interface DataSource {
    List<String> getFilePaths(String directoryPath);
}
