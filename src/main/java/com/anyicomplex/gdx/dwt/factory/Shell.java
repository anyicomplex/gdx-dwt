package com.anyicomplex.gdx.dwt.factory;

import com.badlogic.gdx.utils.Array;

public interface Shell {

    enum ShellType {
        Normal,
        Dialog,
        Tooltip,
        Popup
    }

    void close();

    ShellType type();

    boolean isRootShell();

    Array<Shell> getChildShells();

    default void closeAllChildShells() {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) shell.close();
        }
    }

}
