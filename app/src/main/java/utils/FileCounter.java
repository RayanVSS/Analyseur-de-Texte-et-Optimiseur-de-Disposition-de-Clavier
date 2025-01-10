package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe utilitaire pour la gestion des fichiers.
 */
public class FileCounter {

    /**
     * Liste tous les fichiers dans un repertoire donne.
     *
     * @param directoryPath Chemin du repertoire.
     * @return Liste des fichiers.
     */
    public List<File> listFiles(String directoryPath) {
        File folder = new File(directoryPath);
        List<File> fileList = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Le chemin specifie n'est pas un dossier valide.");
            return fileList;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier trouve dans le dossier.");
            return fileList;
        }

        for (File file : files) {
            if (file.isFile()) {
                fileList.add(file);
            }
        }

        return fileList;
    }

    /**
     * Affiche une liste de fichiers avec des numeros pour selection.
     *
     * @param files Liste des fichiers à afficher.
     */
    public void displayFiles(List<File> files) {
        if (files.isEmpty()) {
            System.out.println("Aucun fichier à afficher.");
            return;
        }

        for (int i = 0; i < files.size(); i++) {
            System.out.println((i + 1) + ". " + files.get(i).getName());
        }
    }

    /**
     * Compte le nombre de caracteres dans un fichier.
     *
     * @param file Le fichier à lire.
     * @return Nombre de caracteres.
     * @throws IOException Si une erreur de lecture se produit.
     */
    public int countCharacters(File file) throws IOException {
        return Readfile.readFile(file.getAbsolutePath()).length();
    }

    /**
     * Permet à l'utilisateur de selectionner un ou plusieurs fichiers dans un
     * repertoire specifique.
     *
     * @param directoryPath Chemin du repertoire contenant les fichiers.
     * @return Liste des chemins absolus des fichiers selectionnes ou une liste vide
     *         si aucun fichier n'est selectionne.
     */
    public List<String> selectFiles(String directoryPath) {
        Scanner scanner = new Scanner(System.in);
        List<File> files = listFiles(directoryPath);
        List<String> selectedFilePaths = new ArrayList<>();

        if (files.isEmpty()) {
            return selectedFilePaths;
        }

        System.out.println("Fichiers disponibles dans " + directoryPath + ":");
        displayFiles(files);

        System.out.println(
                "\nEntrez les numeros des fichiers souhaites separes par des virgules (par exemple: 1,3,5) : ");
        String input = scanner.nextLine();
        String[] parts = input.split(",");

        for (String part : parts) {
            try {
                int choice = Integer.parseInt(part.trim());
                if (choice >= 1 && choice <= files.size()) {
                    File selectedFile = files.get(choice - 1);
                    selectedFilePaths.add(selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Numero de fichier invalide : " + choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide : " + part.trim());
            }
        }

        return selectedFilePaths;
    }

    /**
     * Permet à l'utilisateur de selectionner une disposition de clavier dans le
     * repertoire 'clavier'.
     *
     * @return Chemin absolu du fichier de disposition selectionne ou null si aucun
     *         fichier n'est selectionne.
     */
    public String selectKeyboardLayout() {
        String directoryPath = getTerminalLocation() + "/clavier";
        Scanner scanner = new Scanner(System.in);
        List<File> keyboards = listFiles(directoryPath);
        if (keyboards.isEmpty()) {
            System.out.println("Aucun clavier trouve dans le dossier 'clavier'.");
            return null;
        }

        System.out.println("Claviers disponibles :");
        displayFiles(keyboards);

        int choice = -1;
        do {
            System.out.print("Entrez le numero du clavier souhaite : ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > keyboards.size()) {
                    System.out.println("Entree invalide ! Un nombre entre 1 et " + keyboards.size() + " est attendu.");
                }
            } else {
                System.out.println("Entree invalide ! Un entier est attendu.");
                scanner.next(); // Consommer l'entree invalide
            }
        } while (choice < 1 || choice > keyboards.size());

        File selectedKeyboard = keyboards.get(choice - 1);
        return selectedKeyboard.getAbsolutePath();
    }

    /**
     * Retourne le chemin du repertoire terminal.
     *
     * @return Chemin du repertoire courant.
     */
    public static String getTerminalLocation() {
        return System.getProperty("user.dir");
    }

    /**
     * Methode principale pour compter les caracteres dans tous les fichiers du
     * dossier 'texte'.
     */
    public void runFileCounter() {
        String directoryPath = getTerminalLocation() + "/texte";
        List<File> files = listFiles(directoryPath);
        if (files.isEmpty()) {
            return;
        }

        for (File file : files) {
            try {
                int characterCount = countCharacters(file);
                System.out.println("Fichier : " + file.getName() + " | Nombre de caracteres : " + characterCount);
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture du fichier : " + file.getName());
            }
        }
    }
}
