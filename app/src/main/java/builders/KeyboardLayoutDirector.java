package builders;

import models.KeyboardLayout;

/**
 * Directeur qui utilise le Builder pour construire un KeyboardLayout.
 */
public class KeyboardLayoutDirector {
    private KeyboardLayoutBuilder builder;

    /**
     * Definit le Builder à utiliser.
     *
     * @param builder Le monteur de KeyboardLayout.
     */
    public void setBuilder(KeyboardLayoutBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construit le KeyboardLayout en utilisant le Builder defini.
     *
     * @return Le KeyboardLayout construit.
     */
    public KeyboardLayout constructKeyboardLayout() {
        builder.buildDisposition();
        return builder.getKeyboardLayout();
    }
}
