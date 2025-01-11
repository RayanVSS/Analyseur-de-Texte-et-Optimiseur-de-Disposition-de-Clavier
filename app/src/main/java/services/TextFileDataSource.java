package services;

import utils.FileCounter;

import java.util.List;

/**
 * Implementation de DataSource pour les fichiers texte.
 */
public class TextFileDataSource implements DataSource {

    private FileCounter fileCounter;

    /**
     * Constructeur qui initialise le FileCounter.
     */
    public TextFileDataSource() {
        this.fileCounter = new FileCounter();
    }

    @Override
    public List<String> getFilePaths(String directoryPath) {
        return fileCounter.selectFiles(directoryPath);
    }
}
