package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe utilitaire pour la gestion des fichiers.
 */
public class FileCounter {

    /**
     * Retourne le chemin du repertoire courant.
     *
     * @return Chemin du repertoire courant.
     */
    public static String getTerminalLocation() {
        return System.getProperty("user.dir");
    }

    /**
     * Permet à l'utilisateur de selectionner des fichiers dans un repertoire donne.
     *
     * @param directoryPath Chemin du repertoire contenant les fichiers.
     * @return Liste des chemins des fichiers selectionnes.
     */
    public List<String> selectFiles(String directoryPath) {
        List<String> selectedFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Le repertoire specifie n'existe pas ou n'est pas un repertoire.");
            return selectedFiles;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier texte disponible dans le repertoire.");
            return selectedFiles;
        }

        System.out.println("Fichiers disponibles dans " + directoryPath + ":");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out
                .print("\nEntrez les numeros des fichiers souhaites separes par des virgules (par exemple: 1,3,5) : ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] parts = input.split(",");

        for (String part : parts) {
            try {
                int index = Integer.parseInt(part.trim()) - 1;
                if (index >= 0 && index < files.length) {
                    selectedFiles.add(files[index].getAbsolutePath());
                } else {
                    System.out.println("Numero de fichier invalide : " + (index + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide : " + part);
            }
        }

        return selectedFiles;
    }

    /**
     * Permet à l'utilisateur de selectionner une disposition de clavier dans le
     * repertoire /clavier.
     *
     * @return Chemin du fichier de disposition selectionne ou null si aucun
     *         selectionne.
     */
    public String selectKeyboardLayout() {
        String clavierDir = getTerminalLocation() + "/clavier";
        File directory = new File(clavierDir);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Le repertoire des claviers n'existe pas ou n'est pas un repertoire.");
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("Aucune disposition de clavier disponible.");
            return null;
        }

        System.out.println("Dispositions de clavier disponibles dans " + clavierDir + " :");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nEntrez le numero de la disposition de clavier souhaitee (ou 0 pour annuler) : ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int choice;
        try {
            choice = Integer.parseInt(input.trim());
            if (choice == 0) {
                return null;
            }
            if (choice < 1 || choice > files.length) {
                System.out.println("Numero de disposition invalide.");
                return null;
            }
            return files[choice - 1].getAbsolutePath();
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return null;
        }
    }

    /**
     * Execute un compteur de caracteres dans tous les fichiers texte du repertoire
     * /texte.
     */
    public void runFileCounter() {
        String texteDir = getTerminalLocation() + "/texte";
        List<String> selectedFiles = selectFiles(texteDir);
        if (selectedFiles.isEmpty()) {
            System.out.println("Aucun fichier selectionne.");
            return;
        }

        for (String filePath : selectedFiles) {
            try {
                String content = Readfile.readFile(filePath);
                int charCount = content.length();
                System.out.println(
                        "Fichier : " + new File(filePath).getName() + " - Nombre de caracteres : " + charCount);
            } catch (Exception e) {
                System.out.println("Erreur lors du comptage des caracteres du fichier : " + filePath);
                e.printStackTrace();
            }
        }
    }
}
