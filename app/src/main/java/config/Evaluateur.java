package config;

import java.util.HashMap;

public class Evaluateur {

    private HashMap<String, Integer> nGramMap; // Fréquences des caractères ou N-grammes
    private HashMap<Character, String> disposition; // Disposition des touches (touche -> doigt)
    private HashMap<String, Integer> scores; // Scores par type de mouvement

    public Evaluateur(HashMap<String, Integer> nGramMap, HashMap<Character, String> disposition) {
        this.nGramMap = nGramMap;
        this.disposition = disposition;
        this.scores = new HashMap<>();
    }

    public void evaluer() {
        for (String nGram : nGramMap.keySet()) {
            int freq = nGramMap.get(nGram);
            String mouvement = analyserMouvement(nGram);
            scores.put(mouvement, scores.getOrDefault(mouvement, 0) + freq);
        }
    }

    private String analyserMouvement(String nGram) {
        if (nGram.length() == 1) {
            return disposition.getOrDefault(nGram.charAt(0), "inconnu");
        } else if (nGram.length() == 2) {
            return analyserBigramme(nGram);
        } else if (nGram.length() == 3) {
            return analyserTrigramme(nGram);
        }
        return "inconnu";
    }

    private String analyserBigramme(String bigramme) {
        char c1 = bigramme.charAt(0);
        char c2 = bigramme.charAt(1);

        String doigt1 = disposition.getOrDefault(c1, "inconnu");
        String doigt2 = disposition.getOrDefault(c2, "inconnu");

        if (doigt1.equals(doigt2)) {
            return "SFB"; // Same Finger Bigram
        }
        return doigt1.equals("main gauche") && doigt2.equals("main droite") ? "alternance" : "autre";
    }

    private String analyserTrigramme(String trigramme) {
        // Ajouter une logique similaire à celle des bigrammes
        return "trigramme générique";
    }

    public void afficherScores() {
        System.out.println("\nResultats de l'evaluation :");
        for (String critere : scores.keySet()) {
            System.out.println(critere + " : " + scores.get(critere));
        }
    }

}
