package services.observer;

/**
 * Observateur pour enregistrer les evenements dans un journal de bord.
 */
public class LoggerObserver implements Observer {
    @Override
    public void update(String eventType, Object data) {
        switch (eventType) {
            case "FileAnalyzed":
                System.out.println("[Logger] Fichier analyse : " + data);
                break;
            case "AnalysisCompleted":
                System.out.println("[Logger] Analyse complete terminee. Resultats dans : " + data);
                break;
            case "KeyboardEvaluated":
                System.out.println("[Logger] l'analyse de frequence est terminee. clavier utiliser : " + data);
                break;
            case "OptimizationKeyBoard":
                System.out.println("[Logger] Optimisation est terminee.");
                break;

            case "StatisticsLoaded":
                System.out.println("[Logger] Statistiques chargees.");
                break;

            case "StatisticsNotFound":
                System.out.println("[Logger] Statistiques non trouvees.");
                break;
            default:
                System.out.println("[Logger] evenement inconnu : " + eventType);
        }
    }
}
