package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3FontHandle;

import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.User32.GWL_STYLE;

public class WindowsNatives {

    public static native Lwjgl3FontHandle[] getSystemFonts();

    public static native Lwjgl3FontHandle getDefaultFont();

    public static void hideWindowButtons(long hWnd, boolean maximize, boolean minimize) {
        long windowLongPtr = GetWindowLongPtr(hWnd, GWL_STYLE);
        if (maximize) windowLongPtr = windowLongPtr & ~WS_MAXIMIZEBOX;
        else windowLongPtr = windowLongPtr | WS_MAXIMIZEBOX;
        if (minimize) windowLongPtr = windowLongPtr & ~WS_MINIMIZEBOX;
        else windowLongPtr = windowLongPtr | WS_MINIMIZEBOX;
        SetWindowLongPtr(hWnd, GWL_STYLE, windowLongPtr);
    }

    public static native void grabPointer(long hWnd);

    public static native void ungrabPointer();

    public static native void setWindowIsDialog(long hWnd, long parent);

    public static native void setWindowIsNormal(long hWnd);

    public static native void setWindowIsTooltip(long hWnd, long parent);

    public static native void setWindowIsPopup(long hWnd, long parent);

    public static native void open(String path);

}
