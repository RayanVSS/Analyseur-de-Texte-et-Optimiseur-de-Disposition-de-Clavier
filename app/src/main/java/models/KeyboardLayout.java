package models;

import config.Evaluateur.TouchInfo;
import java.util.HashMap;

/**
 * Classe pour représenter la disposition des touches du clavier.
 */
public class KeyboardLayout {
    private HashMap<Character, TouchInfo> disposition;

    public KeyboardLayout() {
        this.disposition = new HashMap<>();
    }

    public void addTouch(char key, TouchInfo touchInfo) {
        this.disposition.put(key, touchInfo);
    }

    public HashMap<Character, TouchInfo> getDisposition() {
        return disposition;
    }
}
