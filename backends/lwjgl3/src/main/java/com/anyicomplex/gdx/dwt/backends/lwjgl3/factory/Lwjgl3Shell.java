package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.factory.Shell;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Lwjgl3Shell implements Shell {

    public Lwjgl3Shell(Lwjgl3Window window) {
        if (window == null) throw new GdxRuntimeException("window cannot be null.");
        this.window = window;
    }

    private final Lwjgl3Window window;

    public Lwjgl3Window getWindow() {
        return window;
    }

}
