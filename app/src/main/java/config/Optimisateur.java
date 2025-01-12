package config;

import utils.ConsoleUtils;
import utils.FileCounter;
import utils.Jsonfile;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.*;
import java.awt.*;


public class Optimisateur {
    
    private HashMap<String, Integer> nGramMap;
    private static final int TAILLE_POOL = Integer.MAX_VALUE ;
    private static final int MAX_ITERATIONS = 100;
    private ConcurrentLinkedQueue<DispositionCandidate> pool;
    private double scoreInitial;
    private HashMap<Character, Evaluateur.TouchInfo> meilleureDisposition;
    private final Object lock = new Object();

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
    }

    
    private class MultiOpti implements Runnable {
        private int nbIterations = 0; 

        @Override
        public void run() {
            while (nbIterations < MAX_ITERATIONS) { 
                synchronized (lock) {
                    // Attendre si le pool est trop grand
                    if(pool.size() >= TAILLE_POOL) {
                       return;
                    }
                }

                Random random = new Random();
                List<DispositionCandidate> poollocal = pool.stream().toList();
                
                if (random.nextBoolean()) {
                    DispositionCandidate nouveau = mutation(poollocal.get(random.nextInt(pool.size())));
                    addToPool(nouveau);
                } else {
                    DispositionCandidate parentA = poollocal.get(random.nextInt(pool.size()));
                    DispositionCandidate parentB = poollocal.get(random.nextInt(pool.size()));
                    DispositionCandidate nouveau = croisement(parentA, parentB);
                    addToPool(nouveau);
                }
                nbIterations++;
            }
        }
    }
    
    public Optimisateur(HashMap<String, Integer> nGramMap) {
        this.nGramMap = nGramMap;
        this.pool = new ConcurrentLinkedQueue<>();
    }
    
    public void optimiser() {
        initialise_pool();
        if (pool.isEmpty()) {
            System.out.println("Une erreur s'est produite lors de l'initialisation de la disposition de base. L'optimisation est annulée.");
            return;
        }

        System.out.println("Score initial: " + scoreInitial * 100 + "%");

        
        Thread[] threads = new Thread[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new MultiOpti());
            threads[i].start();
        }
        
        System.out.println("Attente de la fin de l'optimisation...");
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
       
        ConsoleUtils.clear();
        double scoreFinal = scoreInitial;
        for (DispositionCandidate disp : pool) {
            if (disp.score > scoreFinal) {
                scoreFinal = disp.score;
                meilleureDisposition = disp.disposition;
            }
        }

        System.out.println("Score final: " + scoreFinal * 100 + "%");
        afficherClavier(meilleureDisposition);
    }
    
    private void initialise_pool() {
        HashMap<Character, Evaluateur.TouchInfo> dispositionDeBase = chargerDispositionDeBase();
        if(dispositionDeBase == null) {
            return;
        }
        pool.clear();
        pool.add(new DispositionCandidate(dispositionDeBase));
        scoreInitial = pool.peek().score;
        int random = new Random().nextInt(dispositionDeBase.keySet().size());
        HashMap<Character, Evaluateur.TouchInfo> nouvelleDisp = new HashMap<>(dispositionDeBase);
        for (int j = 0; j < random ; j++) { 
            permuterDeuxTouchesAleatoires(nouvelleDisp);
        }
        pool.add(new DispositionCandidate(nouvelleDisp));
        
    }

    private void addToPool(DispositionCandidate nouveau) {
        synchronized(lock) {
            pool.add(nouveau);
            lock.notifyAll();
        }
    }
    
    private DispositionCandidate mutation(DispositionCandidate parent) {
        HashMap<Character, Evaluateur.TouchInfo> nouvelleDisp = new HashMap<>(parent.disposition);
        int random = new Random().nextInt(nouvelleDisp.keySet().size());
        for (int i = 0; i < random; i++) {
            permuterDeuxTouchesAleatoires(nouvelleDisp);
        }
        return new DispositionCandidate(nouvelleDisp);
    }
    
    private DispositionCandidate croisement(DispositionCandidate parentA, DispositionCandidate parentB) {
        // Copie profonde des dispositions
        HashMap<Character, Evaluateur.TouchInfo> enfant = new HashMap<>();
        HashMap<Character, Evaluateur.TouchInfo> nonUtilises = new HashMap<>(parentB.disposition);
        List<Character> touchesRestantes = new ArrayList<>(parentA.disposition.keySet());
        Random random = new Random();
    
        // Sélectionner aléatoirement la moitié des touches du parent A
        int moitie = touchesRestantes.size() / 2;
        while (enfant.size() < moitie) {
            Character c = touchesRestantes.get(random.nextInt(touchesRestantes.size()));
            Evaluateur.TouchInfo info = parentA.disposition.get(c);
    
            // Vérifier si le caractère est une lettre
            boolean possible = sansShift(c);
    
            // Vérifier si le touch est déjà occupé par un caractère incompatible
            boolean conflit = false;
            for (Map.Entry<Character, Evaluateur.TouchInfo> entry : enfant.entrySet()) {
                if (entry.getValue().equals(info)) {
                    if (possible != sansShift(entry.getKey())) {
                        conflit = true;
                        break;
                    }
                }
            }
    
            if (conflit) {
                continue;
            }
    
            if (possible) {
                if (!toucheContientLettre(info, enfant) && !info.isShift()) {
                    enfant.put(c, info);
                    nonUtilises.remove(c);
                    touchesRestantes.remove(c);
                }
            } else {
                if (!toucheContientLettre(info, enfant)) {
                    enfant.put(c, info);
                    nonUtilises.remove(c);
                    touchesRestantes.remove(c);
                }
            }
        }
    

        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : nonUtilises.entrySet()) {
            Character c = entry.getKey();
            Evaluateur.TouchInfo info = entry.getValue();
            boolean possible = sansShift(c);
    
            boolean conflit = false;
            for (Map.Entry<Character, Evaluateur.TouchInfo> e : enfant.entrySet()) {
                if (e.getValue().equals(info)) {
                    if (possible != sansShift(e.getKey())) {
                        conflit = true;
                        break;
                    }
                }
            }
    
            if (conflit) {
                continue; // Passer si conflit détecté
            }
    
            if (possible) {
                if (!enfant.containsValue(info) && !toucheContientLettre(info, enfant) && !info.isShift()) {
                    enfant.put(c, info);
                    touchesRestantes.remove(c);
                }
            } else if (!enfant.containsValue(info) && !toucheContientLettre(info, enfant)) {
                enfant.put(c, info);
                touchesRestantes.remove(c);
            }
        }
    
        // Gérer les touches restantes avec des positions libres
        Set<Evaluateur.TouchInfo> positionsUtilisees = new HashSet<>(enfant.values());
        List<Evaluateur.TouchInfo> positionsLibres = new ArrayList<>();
    
        // Collecter toutes les positions disponibles de parentA
        for (Evaluateur.TouchInfo info : parentA.disposition.values()) {
            if (!positionsUtilisees.contains(info)) {
                positionsLibres.add(info);
            }
        }
    
        for (Character c : touchesRestantes) {
            if (!positionsLibres.isEmpty()) {
                Evaluateur.TouchInfo originalInfo = parentA.disposition.get(c);
                List<Evaluateur.TouchInfo> candidates = new ArrayList<>();
                for (Evaluateur.TouchInfo libre : positionsLibres) {
                    if (libre.isShift() == originalInfo.isShift()) {
                        boolean possible = sansShift(c);
                        boolean compatible = true;
                        for (Map.Entry<Character, Evaluateur.TouchInfo> e : enfant.entrySet()) {
                            if (e.getValue().equals(libre)) {
                                if (possible != sansShift(e.getKey())) {
                                    compatible = false;
                                    break;
                                }
                            }
                        }
                        if (compatible) {
                            candidates.add(libre);
                        }
                    }
                }
    
                if (!candidates.isEmpty()) {
                    int idx = random.nextInt(candidates.size());
                    Evaluateur.TouchInfo selected = candidates.get(idx);
                    enfant.put(c, selected);
                    positionsLibres.remove(selected);
                }
            }
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
    
        if (info1.isShift() != info2.isShift()) return;
    
        boolean possible1 = sansShift(touche1);
        boolean possible2 = sansShift(touche2);
    
        if (possible1 != possible2) return;

        if (possible1) {
            if (info2.isShift()) return;
        }
    
        disposition.put(touche1, info2);
        disposition.put(touche2, info1);
    }

    private boolean sansShift (char c) {
        return (c >= 'a' && c <= 'z') || c=='↑';
    }

    private boolean toucheContientLettre(Evaluateur.TouchInfo touchInfo, HashMap<Character, Evaluateur.TouchInfo> disposition) {
        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : disposition.entrySet()) {
            if (touchInfo.memetouches(entry.getValue()) && sansShift(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    private HashMap<Character, Evaluateur.TouchInfo> chargerDispositionDeBase() {
        String cheminDisposition = FileCounter.getTerminalLocation() + "/clavier/azerty.json";
        return Jsonfile.loadDispositionFromJson(cheminDisposition);
    }
    
    public void afficherClavier(HashMap<Character, Evaluateur.TouchInfo> disposition) {
        JFrame frame = new JFrame("Disposition du clavier");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel keyboardPanel = new JPanel(new GridLayout(4, 13, 10, 10)); 
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); 
    
        JButton[][] touches = new JButton[4][13];
        Map<String, StringBuilder> positionMap = new HashMap<>();
        Map<String, StringBuilder> shiftMap = new HashMap<>();
    
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                touches[i][j] = new JButton();
                touches[i][j].setPreferredSize(new Dimension(120, 120));
                touches[i][j].setFont(new Font("Arial", Font.BOLD, 18)); 
                keyboardPanel.add(touches[i][j]);
            }
        }
    
        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : disposition.entrySet()) {
            Character c = entry.getKey();
            Evaluateur.TouchInfo info = entry.getValue();
            String posKey = info.getRangee() + "," + info.getColonne();
    
            String charStr = c.toString();

            if(charStr!=null){
                // Échapper les caractères spéciaux pour HTML
                charStr.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
            }
    
            if (info.isShift()) {
                shiftMap.computeIfAbsent(posKey, k -> new StringBuilder()).append(charStr);
            } else {
                positionMap.computeIfAbsent(posKey, k -> new StringBuilder()).append(charStr);
            }
        }
    
    
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                String posKey = i + "," + j;
                StringBuilder normalText = positionMap.get(posKey);
                StringBuilder shiftText = shiftMap.get(posKey);
    
                if (normalText != null || shiftText != null) {
                    String html = "<html><center>";
                    
                    if (shiftText != null && shiftText.length() > 0) {
                        html += "<span style='color:blue;font-size:16px;'>" + shiftText.toString() + "</span><br>";
                    }
                    
                    if (normalText != null && normalText.length() > 0) {
                        html += "<b style='font-size:20px;'>" + normalText.toString() + "</b>";
                    }
                    
                    html += "</center></html>";
                    touches[i][j].setText(html);
                }
            }
        }
    
        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void sauvegarderDisposition () {
        String cheminDisposition = FileCounter.getTerminalLocation() + "/resultat/optimise.json";
        Jsonfile.createJsonFromDisposition(meilleureDisposition, cheminDisposition);
    }
}