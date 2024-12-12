package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class FileCounter {

    // lire tous les fichiers dans un dossier et afficher leur nombre de caractères
    public void listFiles(String directoryPath) {
        File folder = new File(directoryPath);

        //chemin donne est un dossier
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Le chemin spécifié n'est pas un dossier valide.");
            return;
        }

        // tous les fichiers dans le dossier
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier trouvé dans le dossier.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                try {
                    int characterCount = countCharacters(file);
                    System.out.println("Fichier : " + file.getName() + " | Nombre de caracteres : " + characterCount);
                } catch (IOException e) {
                    System.out.println("Erreur lors de la lecture du fichier : " + file.getName());
                }
            }
        }
    }

    //compter le nombre de caractères 
    private int countCharacters(File file) throws IOException {
        int characterCount = 0;

        try (FileReader reader = new FileReader(file)) {
            int c;
            while ((c = reader.read()) != -1) {
                characterCount++;
            }
        }

        return characterCount;
    }

    public static String getTerminalLocation() {
        return System.getProperty("user.dir");
    }


    public static String FileSelector() {
        Scanner scanner = new Scanner(System.in);
        FileCounter.FileCounterRun();
        System.out.println("Entrez le chemin du fichier : ");
        String filePath = scanner.nextLine();
        File file = new File(getTerminalLocation() + "/texte/" + filePath);
        if (!file.exists()) {
            System.out.println("Le fichier n'existe pas.");
            return null;
        }
        return getTerminalLocation()+ "/texte/" + filePath;
    }

    // Méthode principale pour tester la classe
    public static void FileCounterRun() {
        FileCounter counter = new FileCounter();
        String directoryPath = getTerminalLocation()+ "/texte"; 
        counter.listFiles(directoryPath);
    }

}
