package config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluateur {

    /*
     * classe interne pour stocker les informations sur les touches
     * rangee : la rangee de la touche
     * colonne : la colonne de la touche
     * doigt : le doigt utilise pour appuyer sur la touche
     * home : position de doigt au repos
     */
    public static class TouchInfo {
        private int rangee;
        private int colonne;
        private String doigt;
        private boolean home;

        // Ajout du champ shift en optionnel :
        // Il peut etre absent du JSON, dans ce cas on le considere false par defaut.
        private boolean shift;

        // Constructeur :
        public TouchInfo(int rangee, int colonne, String doigt, boolean home, boolean shift) {
            this.rangee = rangee;
            this.colonne = colonne;
            this.doigt = doigt;
            this.home = home;
            this.shift = shift;
        }

        public int getRangee() {
            return rangee;
        }

        public int getColonne() {
            return colonne;
        }

        public String getDoigt() {
            return doigt;
        }

        public boolean isHome() {
            return home;
        }

        public boolean isShift() {
            return shift;
        }
    }

    private HashMap<String, Integer> nGramMap; // Map des n-grammes
    private HashMap<Character, TouchInfo> disposition; // Map du clavier

    /**
     * Map des scores a utiliser pour la partie 3 avec:
     * - inconue (rien)
     * - SFB (+) (same finger bigram)
     * - ciseaux (+)
     * - LSB (+) (left shift bigram)
     * - alternance (+)
     * - roulement(+)
     * - mauvaise redirection (+)
     * - redirection (+)
     * - SKS (+) (same key same finger)
     * - autre_bigramme (-)
     * - autre_trigramme (-)
     * - tout les doitgs du clavier (depend du pourcentage)
     */
    private HashMap<String, Integer> scores;

    // Compteurs des mains
    private int countLeftHand = 0;
    private int countRightHand = 0;

    // Compteurs par doigt
    private HashMap<String, Integer> fingerCount = new HashMap<>();

    public Evaluateur(HashMap<String, Integer> nGramMap, HashMap<Character, TouchInfo> disposition) {
        this.nGramMap = nGramMap;
        this.disposition = disposition;
        this.scores = new HashMap<>();
    }

    /**
     * evalue les n-grammes et stocke les scores dans la map scores
     * NE FAIT PAS L'AFFICHAGE DES SCORES
     */
    public void evaluer() {
        for (Map.Entry<String, Integer> entry : nGramMap.entrySet()) {
            String nGram = entry.getKey();
            int freq = entry.getValue();
            List<Character> expanded = expandNgram(nGram);

            // si la sequence depasse 3 touches, on ignore
            if (expanded.size() == 0 || expanded.size() > 3) {
                continue;
            }

            List<String> categories;
            switch (expanded.size()) {
                case 1:
                    categories = analyserMonogramme(String.valueOf(expanded.get(0)), freq);
                    break;
                case 2:
                    categories = analyserBigramme("" + expanded.get(0) + expanded.get(1), freq);
                    break;
                case 3:
                    categories = analyserTrigramme("" + expanded.get(0) + expanded.get(1) + expanded.get(2), freq);
                    break;
                default:
                    categories = new ArrayList<>();
                    categories.add("inconnu");
            }

            for (String cat : categories) {
                scores.put(cat, scores.getOrDefault(cat, 0) + freq);
            }
        }
    }

    // Nouvelle methode pour transformer un n-gramme en liste de caracteres
    // tout en gerant la transformation des majuscules en minuscules (sans SHIFT)
    // et l'option shift=true eventuellement definie dans le JSON.
    private List<Character> expandNgram(String ngram) {
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < ngram.length(); i++) {
            char c = ngram.charAt(i);
            List<Character> expandedChars = convertChar(c);
            result.addAll(expandedChars);
        }
        return result;
    }

    /**
     * Convertit un caractere en sa (ou ses) touche(s) de base.
     * Les modifications pour les majuscules :
     * - Si c est majuscule, on le convertit directement en minuscule
     * Pour shift=true dans le JSON on ajoute char SHIFT '¤',
     */
    private List<Character> convertChar(char c) {
        List<Character> list = new ArrayList<>();

        if (Character.isUpperCase(c)) {
            c = Character.toLowerCase(c);
        }
        TouchInfo info = disposition.get(c);

        if (info == null) {
            list.add(c);
            return list;
        }
        // si shift est active pour ce caractere => on ajoute SHIFT en plus
        // '¤' pour SHIFT
        if (info.isShift()) {
            list.add('¤');
        }
        // on ajoute le caractere final
        list.add(c);
        return list;
    }

    /**
     * Analyse un monogramme
     * elle va :
     * - analyser le doigt utilise pour appuyer sur la touche
     * - incrementer les compteurs des mains
     * - incrementer les compteurs des doigts
     * - retourner le doit utilise ou "inconnu" si la touche n'est pas dans la
     * disposition
     */
    private List<String> analyserMonogramme(String mono, int freq) {
        List<String> categories = new ArrayList<>();
        char c = mono.charAt(0);
        TouchInfo info = disposition.get(c);
        if (info == null) {
            categories.add("inconnu");
            return categories;
        }

        // compteurs des mains
        if (isLeftHand(info)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        // compteur du doigt
        fingerCount.put(info.getDoigt(), fingerCount.getOrDefault(info.getDoigt(), 0) + freq);

        categories.add(info.getDoigt());
        return categories;
    }

    /**
     * Analyse un Bigramme
     * elle va :
     * - incrementer les compteurs des mains
     * - incrementer les compteurs des doigts
     * - "inconue" si une des touches n'est pas dans la disposition
     * - si c'est un ciseaux
     * - si c'est un LSB
     * - si c'est un SFB
     * - si c'est un roulement
     * - si c'est une alternance
     * - si c'est un autre bigramme (donc un mauvement correcte)
     */
    private List<String> analyserBigramme(String bigramme, int freq) {
        List<String> categories = new ArrayList<>();
        char c1 = bigramme.charAt(0);
        char c2 = bigramme.charAt(1);

        TouchInfo i1 = disposition.get(c1);
        TouchInfo i2 = disposition.get(c2);

        if (i1 == null || i2 == null) {
            categories.add("inconnu");
            return categories;
        }

        // Incrementer les compteurs des mains
        if (isLeftHand(i1)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        if (isLeftHand(i2)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        // Incrementer les compteurs des doigts
        fingerCount.put(i1.getDoigt(), fingerCount.getOrDefault(i1.getDoigt(), 0) + freq);
        fingerCount.put(i2.getDoigt(), fingerCount.getOrDefault(i2.getDoigt(), 0) + freq);

        boolean sameFinger = i1.getDoigt().equals(i2.getDoigt());
        boolean sameHand = (isLeftHand(i1) == isLeftHand(i2));
        boolean ciseaux = isCiseaux(i1, i2);
        boolean sfb = sameFinger;
        boolean lsb = isLSB(i1, i2);
        boolean alternance = !sameHand;
        boolean roulement = sameHand && isRoulement(i1, i2);

        if (sfb)
            categories.add("SFB");
        if (ciseaux)
            categories.add("ciseaux");
        if (lsb)
            categories.add("LSB");
        if (alternance)
            categories.add("alternance");
        if (roulement)
            categories.add("roulement");

        if (categories.isEmpty())
            categories.add("autre_bigramme");
        return categories;
    }

    /**
     * Analyse un Trigramme
     * elle va :
     * - incrementer les compteurs des mains
     * - incrementer les compteurs des doigts
     * - "inconue" si une des touches n'est pas dans la disposition
     * - si c'est une mauvaise redirection
     * - si c'est une redirection
     * - si c'est un SKS
     * - si c'est un autre trigramme (donc un mauvement correcte)
     */
    private List<String> analyserTrigramme(String trigramme, int freq) {
        List<String> categories = new ArrayList<>();
        char c1 = trigramme.charAt(0);
        char c2 = trigramme.charAt(1);
        char c3 = trigramme.charAt(2);

        TouchInfo i1 = disposition.get(c1);
        TouchInfo i2 = disposition.get(c2);
        TouchInfo i3 = disposition.get(c3);

        if (i1 == null || i2 == null || i3 == null) {
            categories.add("inconnu");
            return categories;
        }

        // Incrementer les compteurs des mains
        if (isLeftHand(i1)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        if (isLeftHand(i2)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        if (isLeftHand(i3)) {
            countLeftHand += freq;
        } else {
            countRightHand += freq;
        }

        // Incrementer les compteurs des doigts
        fingerCount.put(i1.getDoigt(), fingerCount.getOrDefault(i1.getDoigt(), 0) + freq);
        fingerCount.put(i2.getDoigt(), fingerCount.getOrDefault(i2.getDoigt(), 0) + freq);
        fingerCount.put(i3.getDoigt(), fingerCount.getOrDefault(i3.getDoigt(), 0) + freq);

        boolean redirection = isRedirection(i1, i2, i3);
        boolean mauvaiseRedirection = redirection && noIndexInTriple(i1, i2, i3);
        boolean sks = isSKS(i1, i2, i3);

        if (mauvaiseRedirection)
            categories.add("mauvaise_redirection");
        if (redirection)
            categories.add("redirection");
        if (sks)
            categories.add("SKS");
        if (categories.isEmpty())
            categories.add("autre_trigramme");
        return categories;
    }

    private boolean isLeftHand(TouchInfo info) {
        return info.getDoigt().contains("gauche");
    }

    private boolean isCiseaux(TouchInfo i1, TouchInfo i2) {
        if (!(isLeftHand(i1) == isLeftHand(i2)))
            return false;
        int r1 = i1.getRangee();
        int r2 = i2.getRangee();
        return (r1 == 1 && r2 == 3) || (r1 == 3 && r2 == 1);
    }

    private boolean isLSB(TouchInfo i1, TouchInfo i2) {
        boolean sameFinger = i1.getDoigt().equals(i2.getDoigt());
        return sameFinger && (!i1.isHome() || !i2.isHome());
    }

    private boolean isRoulement(TouchInfo i1, TouchInfo i2) {
        if (!(isLeftHand(i1) == isLeftHand(i2)))
            return false;
        if (i1.getDoigt().equals(i2.getDoigt()))
            return false;
        return true; // Simplification
    }

    private boolean isRedirection(TouchInfo i1, TouchInfo i2, TouchInfo i3) {
        if (!(isLeftHand(i1) == isLeftHand(i2) && isLeftHand(i2) == isLeftHand(i3)))
            return false;
        int c1 = i1.getColonne();
        int c2 = i2.getColonne();
        int c3 = i3.getColonne();
        return (c1 < c2 && c2 > c3) || (c1 > c2 && c2 < c3);
    }

    private boolean noIndexInTriple(TouchInfo i1, TouchInfo i2, TouchInfo i3) {
        return !i1.getDoigt().contains("index") && !i2.getDoigt().contains("index") && !i3.getDoigt().contains("index");
    }

    private boolean isSKS(TouchInfo i1, TouchInfo i2, TouchInfo i3) {
        return (i1.getDoigt().equals(i3.getDoigt()) && (isLeftHand(i1) != isLeftHand(i2)));
    }

    public void afficherScores() {
        System.out.println("\nResultats de l'evaluation :");

        // Tri en fonction du score
        scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                });

        // Pourcentage d'utilisation des mains
        int total = countLeftHand + countRightHand;
        if (total > 0) {
            double leftPercent = (double) countLeftHand / total * 100;
            double rightPercent = (double) countRightHand / total * 100;
            System.out.println("\nPourcentage d'utilisation des mains :");
            System.out.println("Main gauche : " + String.format("%.2f", leftPercent) + "%");
            System.out.println("Main droite : " + String.format("%.2f", rightPercent) + "%");
        }

        // Affichage des pourcentages d'utilisation des doigts
        System.out.println("\nPourcentage d'utilisation des doigts :");
        fingerCount.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double percent = (double) entry.getValue() / total * 100;
                    System.out.println(entry.getKey() + " : " + String.format("%.2f", percent) + "%");
                });
    }

}
