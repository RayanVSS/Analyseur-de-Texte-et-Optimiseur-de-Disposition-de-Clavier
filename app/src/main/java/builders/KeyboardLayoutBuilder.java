package builders;

import models.KeyboardLayout;

/*
 * interface qui permet de construire un objet KeyboardLayout.
 */
public interface KeyboardLayoutBuilder {
    void buildDisposition();
    KeyboardLayout getKeyboardLayout();
}
