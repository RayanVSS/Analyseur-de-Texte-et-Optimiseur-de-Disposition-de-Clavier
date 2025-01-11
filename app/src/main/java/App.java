import config.Optimisateur;
import services.*;
import services.observer.LoggerObserver;
import utils.ConsoleUtils;

/**
 * Classe principale de l'application.
 */
public class App {
    public static void main(String[] args) {
        ConsoleUtils.clear();
        int choix;
        MenuHandler menuHandler = new MenuHandler();
        DataSource textDataSource = new TextFileDataSource();
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer(textDataSource);
        IFrequencyAnalyzer loggingAnalyzer = new LoggingFrequencyAnalyzerDecorator(frequencyAnalyzer);
        KeyboardEvaluator keyboardEvaluator = new KeyboardEvaluator();
        KeyboardOptimisateur keyboardoptimisateur = new KeyboardOptimisateur();
        TextDisplay textDisplay = new TextDisplay();
        KeyboardDisplay keyboardDisplay = new KeyboardDisplay();
        ResultClearer resultClearer = new ResultClearer();
        

        // Enregistrer un observateur (par exemple, un logger)
        LoggerObserver logger = new LoggerObserver();
        frequencyAnalyzer.registerObserver(logger);
        keyboardEvaluator.registerObserver(logger);
        keyboardoptimisateur.registerObserver(logger);

        do {
            choix = menuHandler.displayMenuAndGetChoice();
            switch (choix) {
                case 1:
                    ConsoleUtils.clear();
                    // Selectionner le nombre d'occurrences
                    int nb_occurence = menuHandler.getIntInRange(1, 4, "Frequence de combien de caracteres : ");
                    // Executer l'analyse de frequence
                    loggingAnalyzer.execute(nb_occurence);
                    break;

                case 2:
                    ConsoleUtils.clear();
                    // Executer l'evaluation de la disposition du clavier
                    keyboardEvaluator.execute();
                    break;

                case 3:
                    ConsoleUtils.clear();
                    keyboardoptimisateur.execute();
                    break;

                case 4:
                    ConsoleUtils.clear();
                    // Executer l'affichage du nombre de caracteres
                    textDisplay.execute();
                    break;

                case 5:
                    ConsoleUtils.clear();
                    // Executer l'affichage des claviers disponibles
                    keyboardDisplay.execute();
                    break;

                case 6:
                    ConsoleUtils.clear();
                    // Executer la vidange des resultats de l'analyseur
                    resultClearer.execute();
                    break;

                case 7:
                    ConsoleUtils.clear();
                    System.out.println("Quitter le programme.");
                    break;

                default:
                    System.out.println("Choix invalide.");
            }
            if (choix != 7) {
                System.out.println("\nAppuyez sur Entree pour continuer...");
                try {
                    System.in.read();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        } while (choix != 7);

        // Fermer proprement tous les services
        frequencyAnalyzer.shutdownExecutor();
        keyboardEvaluator.shutdownExecutor();
        ConsoleUtils.clear();
    }
}
