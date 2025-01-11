package services;

import java.util.Map;

/**
 * Interface pour les analyseurs de frequence.
 */
public interface IFrequencyAnalyzer {
    void execute(int nbOccurence);

    Map<String, Integer> getResults();
}
