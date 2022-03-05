package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3FontHandle;
import com.anyicomplex.xdg.utils.XDGOpen;
import com.anyicomplex.xdg.utils.XDGUtils;

public class LinuxNatives {

    public static native Lwjgl3FontHandle[] getSystemFonts();

    public static native Lwjgl3FontHandle getGtkDefaultFont();

    public static native void hideXWindowButtons(long display, long w, boolean maximize, boolean minimize);

    public static native void setXWindowIsDialog(long display, long w, long parent);

    public static native void setXWindowIsNormal(long display, long w);

    public static native void setXWindowIsTooltip(long display, long w, long parent);

    public static native void setXWindowIsPopup(long display, long w, long parent);

    public static void open(String path) {
        XDGUtils.load();
        XDGOpen.process(null, path);
    }

    public static native int XGrabPointer(long display, long w);

    public static native int XUngrabPointer(long display);

}
