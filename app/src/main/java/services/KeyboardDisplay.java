package services;

import java.io.File;
import utils.FileCounter;

/**
 * Classe pour afficher les claviers disponibles.
 */
public class KeyboardDisplay {

    /**
     * Affiche la liste des dispositions de clavier disponibles.
     */
    public void execute() {
        String clavierDir = FileCounter.getTerminalLocation() + "/clavier";
        File directory = new File(clavierDir);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Le repertoire des claviers n'existe pas ou n'est pas un repertoire.");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("Aucune disposition de clavier disponible.");
            return;
        }

        System.out.println("Dispositions de clavier disponibles dans " + clavierDir + " :");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }
    }
}
