package builders;

import config.Evaluateur;
import models.KeyboardLayout;
import utils.Jsonfile;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonKeyboardLayoutBuilder est une classe qui permet de construire un objet
 * KeyboardLayout à partir d'un fichier JSON.
 * qui contient la disposition des touches du clavier.
 */

public class JsonKeyboardLayoutBuilder implements KeyboardLayoutBuilder {
    private String jsonFilePath;
    private KeyboardLayout keyboardLayout;

    public JsonKeyboardLayoutBuilder(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        this.keyboardLayout = new KeyboardLayout();
    }

    @Override
    public void buildDisposition() {
        HashMap<Character, Evaluateur.TouchInfo> dispoMap = Jsonfile.loadDispositionFromJson(jsonFilePath);
        if (dispoMap != null) {
            for (Map.Entry<Character, Evaluateur.TouchInfo> entry : dispoMap.entrySet()) {
                keyboardLayout.addTouch(entry.getKey(), entry.getValue());
            }
        } else {
            System.out.println("Erreur lors du chargement de la disposition du clavier.");
        }
    }

    @Override
    public KeyboardLayout getKeyboardLayout() {
        return keyboardLayout;
    }
}
