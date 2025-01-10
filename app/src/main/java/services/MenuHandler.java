package services;

import java.util.Scanner;
import utils.ConsoleUtils;

/**
 * Classe pour gerer l'affichage du menu et la recuperation des choix
 * utilisateur.
 */
public class MenuHandler {

    private Scanner scanner;

    public MenuHandler() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Affiche le menu principal et retourne le choix de l'utilisateur.
     *
     * @return Le choix de l'utilisateur.
     */
    public int displayMenuAndGetChoice() {
        int choice = -1;
        do {
            ConsoleUtils.clear();
            System.out.println("\n############################");
            System.out.println("1. Analyseur de frequence de suites de caracteres");
            System.out.println("2. evaluateur de disposition de clavier");
            System.out.println("3. Optimiseur de disposition de clavier");
            System.out.println("4. Texte disponible");
            System.out.println("5. Quitter");
            System.out.println("############################");
            System.out.print("\nVotre choix : ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > 5) {
                    ConsoleUtils.clear();
                    System.out.println("Entree invalide ! Un nombre entre 1 et 5 est attendu.");
                }
            } else {
                ConsoleUtils.clear();
                System.out.println("Entree invalide ! Un entier est attendu.");
                scanner.next(); // Consommer l'entree invalide
            }
        } while (choice < 1 || choice > 5);
        return choice;
    }

    /**
     * Permet à l'utilisateur de selectionner un nombre entier avec des contraintes.
     *
     * @param min    Valeur minimale acceptee.
     * @param max    Valeur maximale acceptee.
     * @param prompt Message à afficher pour l'invite.
     * @return Le nombre entier selectionne.
     */
    public int getIntInRange(int min, int max, String prompt) {
        int choice = -1;
        do {
            System.out.print("\n" + prompt);
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < min || choice > max) {
                    ConsoleUtils.clear();
                    System.out.println("Entree invalide ! Un nombre entre " + min + " et " + max + " est attendu.");
                }
            } else {
                ConsoleUtils.clear();
                System.out.println("Entree invalide ! Un entier est attendu.");
                scanner.next();
            }
        } while (choice < min || choice > max);
        return choice;
    }
}
