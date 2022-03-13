package com.anyicomplex.gdx.dwt.backends.lwjgl3.jnativehook;

import com.github.kwhat.jnativehook.NativeLibraryLocator;

import java.io.File;
import java.util.Iterator;

public class ValidatedLibraryLocator implements NativeLibraryLocator {

    @Override
    public Iterator<File> getLibraries() {
        return null;
    }

}
