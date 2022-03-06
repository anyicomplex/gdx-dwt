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

    public abstract Array<Shell> childShells();

    public void closeAllChildShells() {
        Array<Shell> shells = childShells();
        if (shells != null) {
            for (Shell shell : shells) shell.close();
        }
    }

    public void closeChildShells(ShellType type) {
        Array<Shell> shells = childShells();
        if (shells != null) {
            for (Shell shell : shells) {
                if (shell.type() == type) shell.close();
            }
        }
    }

    public abstract int positionX();

    public abstract int positionY();

    public abstract int width();

    public abstract int height();

    public boolean contains(int screenX, int screenY) {
        int positionX = positionX();
        int positionY = positionY();
        int width = width();
        int height = height();
        return positionX <= screenX && positionX + width >= screenX && positionY <= screenY && positionY + height >= screenY;
    }

}
