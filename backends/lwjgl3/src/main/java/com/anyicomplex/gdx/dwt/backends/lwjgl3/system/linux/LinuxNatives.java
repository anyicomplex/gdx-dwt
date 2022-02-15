package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3Font;
import com.anyicomplex.gdx.dwt.toolkit.Font;

public class LinuxNatives {

    public static Font[] systemFonts() {
        return nsystemFonts();
    }

    public static native Lwjgl3Font[] nsystemFonts();

    public static void hideXWindowButtons(long display, long w, boolean maximize, boolean minimize) {
        nhideXWindowButtons(display, w, maximize ? 1 : 0, minimize ? 1 : 0);
    }

    public static native void nhideXWindowButtons(long display, long w, int maximize, int minimize);

    public static Font getGtkDefaultFont() {
        return ngetGtkDefaultFont();
    }

    public static native Lwjgl3Font ngetGtkDefaultFont();

}
