package services;

import utils.FileCounter;


/**
 * Classe pour gerer l'affichage du nombre de caracteres dans les fichiers
 * texte.
 */
public class TextDisplay {

    private FileCounter fileCounter;

    public TextDisplay() {
        this.fileCounter = new FileCounter();
    }

    /**
     * Execute l'affichage du nombre de caracteres dans tous les fichiers texte.
     */
    public void execute() {
        fileCounter.runFileCounter();
    }
}
