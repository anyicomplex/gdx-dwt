package com.anyicomplex.gdx.dwt.factory;

import com.badlogic.gdx.utils.Array;

public abstract class Shell {

    public enum ShellType {
        Normal,
        Dialog,
        Tooltip,
        Popup
    }

    public abstract void close();

    public abstract ShellType type();

    public abstract boolean isRootShell();

    public abstract Shell parentShell();

    public abstract Array<Shell> getChildShells();

    public void closeAllChildShells() {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) shell.close();
        }
    }

    public void closeChildShells(ShellType type) {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) {
                if (shell.type() == type) shell.close();
            }
        }
    }

    public abstract int positionX();

    public abstract int positionY();

}
