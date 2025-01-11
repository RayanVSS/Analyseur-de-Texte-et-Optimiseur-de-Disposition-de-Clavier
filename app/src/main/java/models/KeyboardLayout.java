package models;

import config.Evaluateur.TouchInfo;
import java.util.HashMap;

/**
 * Classe representant la disposition d'un clavier.
 */
public class KeyboardLayout {
    private HashMap<Character, TouchInfo> disposition;

    public KeyboardLayout() {
        this.disposition = new HashMap<>();
    }

    /**
     * Ajoute une touche à la disposition du clavier.
     *
     * @param key       La touche (caractere).
     * @param touchInfo Informations sur la touche.
     */
    public void addTouch(char key, TouchInfo touchInfo) {
        this.disposition.put(key, touchInfo);
    }

    /**
     * Retourne la disposition du clavier.
     *
     * @return Map de la disposition du clavier.
     */
    public HashMap<Character, TouchInfo> getDisposition() {
        return disposition;
    }
}
