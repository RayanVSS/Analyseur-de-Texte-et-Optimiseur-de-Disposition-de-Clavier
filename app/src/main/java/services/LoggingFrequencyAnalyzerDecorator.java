package services;

import java.util.Map;

/**
 * Decorateur pour ajouter la journalisation à un FrequencyAnalyzer.
 */
public class LoggingFrequencyAnalyzerDecorator implements IFrequencyAnalyzer {
    private IFrequencyAnalyzer wrappedAnalyzer;

    /**
     * Constructeur qui prend en parametre un analyseur de frequence à decorer.
     *
     * @param analyzer L'analyseur de frequence à decorer.
     */
    public LoggingFrequencyAnalyzerDecorator(IFrequencyAnalyzer analyzer) {
        this.wrappedAnalyzer = analyzer;
    }

    @Override
    public void execute(int nbOccurence) {
        System.out.println("Debut de l'analyse de frequence avec nbOccurence = " + nbOccurence);
        wrappedAnalyzer.execute(nbOccurence);
        System.out.println("Fin de l'analyse de frequence.");
    }

    @Override
    public Map<String, Integer> getResults() {
        return wrappedAnalyzer.getResults();
    }
}
