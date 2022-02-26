package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.macosx;

import java.io.IOException;

public class MacOSXNatives {

    public static void open(String path) {
        try {
            new ProcessBuilder().command("open", path).start();
        } catch (IOException ignored) {
        }
    }

}
