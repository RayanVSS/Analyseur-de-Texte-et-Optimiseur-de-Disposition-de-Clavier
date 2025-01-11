import services.*;
import utils.ConsoleUtils;
import services.observer.LoggerObserver;

/**
 * Classe principale de l'application.
 */
public class App {
    public static void main(String[] args) {
        ConsoleUtils.clear();
        int choix;
        MenuHandler menuHandler = new MenuHandler();
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer(new TextFileDataSource());
        IFrequencyAnalyzer loggingAnalyzer = new LoggingFrequencyAnalyzerDecorator(frequencyAnalyzer);
        KeyboardEvaluator keyboardEvaluator = new KeyboardEvaluator();
        TextDisplay textDisplay = new TextDisplay();

        // Enregistrer les observateurs
        LoggerObserver logger = new LoggerObserver();
        frequencyAnalyzer.registerObserver(logger);
        keyboardEvaluator.registerObserver(logger);

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
                    System.out.println("🔒 Fonctionnalite à venir.");
                    break;

                case 4:
                    ConsoleUtils.clear();
                    // Executer l'affichage du nombre de caracteres
                    textDisplay.execute();
                    break;

                case 5:
                    ConsoleUtils.clear();
                    System.out.println("Quitter le programme.");
                    break;

                default:
                    System.out.println("Choix invalide.");
            }
            if (choix != 5) {
                System.out.println("\nAppuyez sur Entree pour continuer...");
                try {
                    System.in.read();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        } while (choix != 5);

        // Fermer proprement tous les services
        frequencyAnalyzer.shutdownExecutor();
        keyboardEvaluator.shutdownExecutor();
        ConsoleUtils.clear();
    }
}
