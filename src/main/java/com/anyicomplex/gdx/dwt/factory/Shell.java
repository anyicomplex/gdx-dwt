package com.anyicomplex.gdx.dwt.factory;

import com.badlogic.gdx.utils.Array;

public interface Shell {

    void close();

    Array<Shell> getChildShells();

    default void closeAllChildShells() {
        Array<Shell> shells = getChildShells();
        if (shells != null) {
            for (Shell shell : shells) shell.close();
        }
    }

}
