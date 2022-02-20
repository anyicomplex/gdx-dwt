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

    Shell parentShell();

    Array<Shell> getChildShells();

    default void closeAllChildShells() {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) shell.close();
        }
    }

    default void closeChildShells(ShellType type) {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) {
                if (shell.type() == type) shell.close();
            }
        }
    }

}
