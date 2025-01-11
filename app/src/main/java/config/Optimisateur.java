package config;

import utils.ConsoleUtils;
import utils.FileCounter;
import utils.Jsonfile;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Optimisateur {
    
    private HashMap<String, Integer> nGramMap;
    private static final int TAILLE_POOL = 20;
    private static final int MAX_ITERATIONS = 10000;
    private List<DispositionCandidate> pool;
    private double scoreInitial;
    private HashMap<Character, Evaluateur.TouchInfo> meilleureDisposition;
    private double meilleurScore;

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
    
    public Optimisateur(HashMap<String, Integer> nGramMap) {
        this.nGramMap = nGramMap;
        this.pool = new ArrayList<>();
    }
    
    public void optimiser() {
        initialise_pool();
        System.out.println("Score initial: " + scoreInitial);

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
            
            if (pool.size()>=TAILLE_POOL) {
                pool.remove(pool.size() - 1);
                pool.add(nouveau);
            }
            else {
                pool.add(nouveau);
            }
            Collections.sort(pool);
            if (pool.get(0).score > meilleurScore) {
                meilleurScore = pool.get(0).score;
                meilleureDisposition = pool.get(0).disposition;
                System.out.println("Iteration " + i + " - Meilleur score: " + pool.get(0).score);
            }
        }
        ConsoleUtils.clear();
        DispositionCandidate meilleur = pool.get(0);
        System.out.println("Meilleur score final: " + meilleur.score);
        afficherClavier(meilleur.disposition);
    }
    
    private void initialise_pool() {
        HashMap<Character, Evaluateur.TouchInfo> dispositionDeBase = chargerDispositionDeBase();
        int random = new Random().nextInt(dispositionDeBase.keySet().size());
        pool.add(new DispositionCandidate(dispositionDeBase));
        scoreInitial=pool.get(0).score;
        for (int i = 0; i < TAILLE_POOL; i++) {
            HashMap<Character, Evaluateur.TouchInfo> nouvelleDisp = new HashMap<>(dispositionDeBase);
            for (int j = 0; j < random ; j++) { 
                permuterDeuxTouchesAleatoires(nouvelleDisp);
            }
            pool.add(new DispositionCandidate(nouvelleDisp));
        }
        Collections.sort(pool);
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
            enfant.put(c, parentA.disposition.get(c));
            nonUtilises.remove(c);
            touchesRestantes.remove(c);
        }
    
        // Ajouter les touches non conflictuelles du parent B
        for (Map.Entry<Character, Evaluateur.TouchInfo> entry : nonUtilises.entrySet()) {
            Character c = entry.getKey();
            Evaluateur.TouchInfo info = entry.getValue();
            if (!enfant.containsValue(info)) {
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
    
        // Placer les touches restantes sur des positions libres
        for (Character c : touchesRestantes) {
            if (!positionsLibres.isEmpty()) {
                int idx = random.nextInt(positionsLibres.size());
                enfant.put(c, positionsLibres.get(idx));
                positionsLibres.remove(idx);
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
        if ((touche1=='↑' && info2.isShift()) || (touche2=='↑' && info1.isShift())) {
            return;
        }
        disposition.put(touche1, info2);
        disposition.put(touche2, info1);
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
        Jsonfile<Character, Evaluateur.TouchInfo> jsonfile = new Jsonfile<>();
        jsonfile.create_json(meilleureDisposition, cheminDisposition);
    }

}
