package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3FontHandle;
import com.anyicomplex.gdx.dwt.toolkit.FontHandle;

public class WindowsNatives {

    public static FontHandle[] systemFonts() {
        return nsystemFonts();
    }

    public static native Lwjgl3FontHandle[] nsystemFonts();

    public static FontHandle getWin32DefaultFont() {
        return ngetWin32DefaultFont();
    }

    public static native Lwjgl3FontHandle ngetWin32DefaultFont();

    public static native void grabPointer(long hWnd);

    public static native void ungrabPointer();

    public static native void setWindowIsDialog(long hWnd, long parent);

    public static native void setWindowIsNormal(long hWnd);

    public static native void setWindowIsTooltip(long hWnd, long parent);

    public static native void setWindowIsPopup(long hWnd, long parent);

    public static native void open(String path);

}
