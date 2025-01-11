package config;

import utils.FileCounter;
import utils.Jsonfile;
import java.util.*;

public class Optimisateur {
    private HashMap<String, Integer> nGramMap;
    private static final int TAILLE_POOL = 20;
    private static final int MAX_ITERATIONS = 100;
    private List<DispositionCandidate> pool;
    
    private class DispositionCandidate implements Comparable<DispositionCandidate> {
        HashMap<Character, Evaluateur.TouchInfo> disposition;
        double score;
        Evaluateur evaluateur;
        
        DispositionCandidate(HashMap<Character, Evaluateur.TouchInfo> disp) {
            this.disposition = new HashMap<>(disp);
            evaluer();
        }
        
        void evaluer() {
            evaluateur = new Evaluateur(nGramMap, disposition);
            evaluateur.evaluer();
            this.score = evaluateur.getScoreTotal();
        }
        
        @Override
        public int compareTo(DispositionCandidate autre) {
            return Double.compare(autre.score, this.score);
        }

        public void afficherScores() {
            evaluateur.afficherScores();
        }
    }
    
    public Optimisateur(HashMap<String, Integer> nGramMap) {
        this.nGramMap = nGramMap;
        this.pool = new ArrayList<>();
    }
    
    public void optimiser() {
        initialise_pool();
        
        Random random = new Random();
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            DispositionCandidate nouveau;
            
            if (random.nextBoolean()) {
                nouveau = mutation(pool.get(random.nextInt(TAILLE_POOL)));
            } else {
                nouveau = croisement(
                    pool.get(random.nextInt(TAILLE_POOL)),
                    pool.get(random.nextInt(TAILLE_POOL))
                );
            }
            
            if (nouveau.score > pool.get(pool.size() - 1).score) {
                pool.remove(pool.size() - 1);
                pool.add(nouveau);
                Collections.sort(pool);
            }
        }
        
        DispositionCandidate meilleur = pool.get(0);
        sauvegarderDisposition(meilleur.disposition);
        clear();
        meilleur.afficherScores();
        afficherClavier(meilleur.disposition);
    }
    
    private void initialise_pool() {
        HashMap<Character, Evaluateur.TouchInfo> dispositionDeBase = chargerDispositionDeBase();
        for (int i = 0; i < TAILLE_POOL; i++) {
            HashMap<Character, Evaluateur.TouchInfo> nouvelleDisp = new HashMap<>(dispositionDeBase);
            for (int j = 0; j < 10; j++) { 
                permuterDeuxTouchesAleatoires(nouvelleDisp);
            }
            pool.add(new DispositionCandidate(nouvelleDisp));
        }
        Collections.sort(pool);
    }
    
    private DispositionCandidate mutation(DispositionCandidate parent) {
        HashMap<Character, Evaluateur.TouchInfo> nouvelleDisp = new HashMap<>(parent.disposition);
        for (int i = 0; i < 5; i++) {
            permuterDeuxTouchesAleatoires(nouvelleDisp);
        }
        return new DispositionCandidate(nouvelleDisp);
    }
    
    private DispositionCandidate croisement(DispositionCandidate parentA, DispositionCandidate parentB) {
        HashMap<Character, Evaluateur.TouchInfo> enfant = new HashMap<>();
        List<Character> touchesRestantes = new ArrayList<>(parentA.disposition.keySet());
        Random random = new Random();
        
        int moitie = touchesRestantes.size() / 2;
        while (enfant.size() < moitie) {
            Character c = touchesRestantes.get(random.nextInt(touchesRestantes.size()));
            enfant.put(c, parentA.disposition.get(c));
            touchesRestantes.remove(c);
        }
        
        for (Character c : touchesRestantes.toArray(new Character[0])) {
            if (!enfant.containsValue(parentB.disposition.get(c))) {
                enfant.put(c, parentB.disposition.get(c));
                touchesRestantes.remove(c);
            }
        }
        
        for (Character c : touchesRestantes.toArray(new Character[0])) {
            if (!enfant.containsValue(parentA.disposition.get(c))) {
                enfant.put(c, parentA.disposition.get(c));
                touchesRestantes.remove(c);
            }
        }
        
        List<Evaluateur.TouchInfo> positionsLibres = new ArrayList<>();
        for (Evaluateur.TouchInfo info : parentA.disposition.values()) {
            if (!enfant.containsValue(info)) {
                positionsLibres.add(info);
            }
        }
        
        for (Character c : touchesRestantes) {
            int idx = random.nextInt(positionsLibres.size());
            enfant.put(c, positionsLibres.get(idx));
            positionsLibres.remove(idx);
        }
        
        return new DispositionCandidate(enfant);
    }
    
    private void permuterDeuxTouchesAleatoires(HashMap<Character, Evaluateur.TouchInfo> disposition) {
        List<Character> touches = new ArrayList<>(disposition.keySet());
        Random random = new Random();
        
        int index1 = random.nextInt(touches.size());
        int index2 = random.nextInt(touches.size());
        
        Character touche1 = touches.get(index1);
        Character touche2 = touches.get(index2);
        Evaluateur.TouchInfo info1 = disposition.get(touche1);
        Evaluateur.TouchInfo info2 = disposition.get(touche2);
        
        disposition.put(touche1, info2);
        disposition.put(touche2, info1);
    }

    private HashMap<Character, Evaluateur.TouchInfo> chargerDispositionDeBase() {
        String cheminDisposition = FileCounter.getTerminalLocation() + "/resultat/azerty.json";
        return Jsonfile.loadDispositionFromJson(cheminDisposition);
    }

    public void afficherClavier(HashMap<Character, Evaluateur.TouchInfo> disposition) {
        Character[][] clavier = new Character[4][13];
        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : disposition.entrySet()) {
            Character c = entry.getKey();
            Evaluateur.TouchInfo info = entry.getValue();
            if (!info.isShift()) {
                clavier[info.getRangee()][info.getColonne()] = c;
            }
        }
        
        System.out.println("\n|---------------------------------------------|");
        

        for (int i = 0; i < 4; i++) {
            System.out.print("| ");
            for (int j = 0; j < 13; j++) {
                if (clavier[i][j] != null) {
                    System.out.print(clavier[i][j] + " ");
                } else {
                    System.out.print("  "); 
                }
            }
            System.out.println("|");
        }
        
        System.out.println("|---------------------------------------------|");
    }
    
    private void sauvegarderDisposition(HashMap<Character, Evaluateur.TouchInfo> disposition) {
        String cheminDisposition = FileCounter.getTerminalLocation() + "/resultat/meilleureDisposition.json";
        Jsonfile<Character, Evaluateur.TouchInfo> jsonfile = new Jsonfile<>();
        jsonfile.create_json(disposition, cheminDisposition);
    }

    
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
