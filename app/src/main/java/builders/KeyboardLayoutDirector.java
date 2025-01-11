package builders;

import models.KeyboardLayout;

/**
 * KeyboardLayoutDirector est une classe qui permet de construire un objet KeyboardLayout.
 */
public class KeyboardLayoutDirector {
    private KeyboardLayoutBuilder builder;

    public void setBuilder(KeyboardLayoutBuilder builder) {
        this.builder = builder;
    }

    public KeyboardLayout constructKeyboardLayout() {
        builder.buildDisposition();
        return builder.getKeyboardLayout();
    }
}
