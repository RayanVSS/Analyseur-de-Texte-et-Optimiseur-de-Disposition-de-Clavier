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
            default:
                System.out.println("[Logger] evenement inconnu : " + eventType);
        }
    }
}
