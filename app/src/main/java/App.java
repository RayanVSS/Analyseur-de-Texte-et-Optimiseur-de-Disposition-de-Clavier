import java.util.Scanner;
import config.Analyseur;
import utils.FileCounter;
import utils.Readfile;

public class App {

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static int menu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            System.out.println("\n############################");
            System.out.println("1. Analyseur de frequence de suites de caracteres");
            System.out.println("2. Un evaluateur de disposition de clavier");
            System.out.println("3. Un optimiseur de disposition de clavier");
            System.out.println("4. Texte disponible");
            System.out.println("5. Quitter");
            System.out.println("############################");
            System.out.print("\nVotre choix : ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > 5) {
                    clear();
                    System.out.println("Entree invalide ! un nombre entre 1 et 5.");
                }
            } else {
                clear();
                System.out.println("Entree invalide ! un entier est attendu.");
                scanner.next(); // Consommer l'entrée invalide
            }
        } while (choice < 1 || choice > 5);
        return choice;
    }

    public static int IntSelector() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            System.out.print("\nfrequence de combien de caractere: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1) {
                    clear();
                    System.out.println("Entree invalide ! un nombre superieur a 0 est attendu.");
                }
                if (choice > 4) {
                    clear();
                    System.out.println("Entree invalide ! un nombre inferieur a 4 est attendu.");
                }
            } else {
                clear();
                System.out.println("Entree invalide ! un entier est attendu.");
                scanner.next(); 
            }
        } while (choice < 1 || choice > 4);
        return choice;
    }

    public static void main(String[] args) {
        clear();
        int choix;
        do {
            choix = menu();
            switch (choix) {
                case 1:
                    clear();
                    String file = FileCounter.FileSelector();
                    if (file == null) break;
                    int nb_occurence = IntSelector();
                    Analyseur analyseur = new Analyseur(Readfile.readFile(file), nb_occurence);
                    analyseur.analyse();
                    analyseur.afficher(analyseur.getMap());
                    break;
                case 2:
                    clear();
                    System.out.println("🔒");
                    break;
                case 3:
                    clear();
                    System.out.println("🔒");
                    break;
                case 4:
                    clear();
                    FileCounter.FileCounterRun();
                    break;
                case 5:
                    clear();
                    System.out.println("Quitter le programme.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        } while (choix != 5);
        clear();
    }
}
