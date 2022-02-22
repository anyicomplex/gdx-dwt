package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3FontHandle;
import com.anyicomplex.gdx.dwt.toolkit.FontHandle;

public class LinuxNatives {

    public static FontHandle[] systemFonts() {
        return nsystemFonts();
    }

    public static native Lwjgl3FontHandle[] nsystemFonts();

    public static void hideXWindowButtons(long display, long w, boolean maximize, boolean minimize) {
        nhideXWindowButtons(display, w, maximize ? 1 : 0, minimize ? 1 : 0);
    }

    public static native void nhideXWindowButtons(long display, long w, int maximize, int minimize);

    public static FontHandle getGtkDefaultFont() {
        return ngetGtkDefaultFont();
    }

    public static native Lwjgl3FontHandle ngetGtkDefaultFont();

    public static native void grabPointer(long display, long w);

    public static native void ungrabPointer(long display);

    public static native void setXWindowIsDialog(long display, long w, long parent);

    public static native void setXWindowIsNormal(long display, long w);

    public static native void setXWindowIsTooltip(long display, long w, long parent);

    public static native void setXWindowIsPopup(long display, long w, long parent);

}
