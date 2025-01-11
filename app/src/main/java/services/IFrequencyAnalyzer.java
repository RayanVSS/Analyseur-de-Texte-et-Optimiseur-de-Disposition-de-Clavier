package services;

import java.util.Map;

/**
 * Interface pour les analyseurs de fréquence.
 */
public interface IFrequencyAnalyzer {
    void execute(int nbOccurence);
    Map<String, Integer> getResults();
}
