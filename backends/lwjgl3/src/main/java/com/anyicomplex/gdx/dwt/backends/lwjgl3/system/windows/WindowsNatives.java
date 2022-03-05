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

    public static void setWindowIsDialog(long hWnd, long parent) {
        long windowLongPtr = GetWindowLongPtr(hWnd, GWL_STYLE);
        windowLongPtr = windowLongPtr | WS_POPUP;
        SetWindowLongPtr(hWnd, GWL_STYLE, windowLongPtr);
        SetWindowLongPtr(hWnd, GWL_HWNDPARENT, parent);
        enableWindow(parent, false);
    }

    public static void setWindowIsNormal(long hWnd) {
        long windowLongPtr = GetWindowLongPtr(hWnd, GWL_STYLE);
        windowLongPtr = windowLongPtr & ~WS_POPUP;
        windowLongPtr = windowLongPtr & ~WS_CHILD;
        windowLongPtr = windowLongPtr | WS_EX_APPWINDOW;
        SetWindowLongPtr(hWnd, GWL_STYLE, windowLongPtr);
        SetWindowLongPtr(hWnd, GWL_HWNDPARENT, 0);
    }

    public static void setWindowIsTooltip(long hWnd, long parent) {
        long windowLongPtr = GetWindowLongPtr(hWnd, GWL_STYLE);
        windowLongPtr = windowLongPtr | WS_CHILD;
        windowLongPtr = windowLongPtr & ~WS_EX_APPWINDOW;
        SetWindowLongPtr(hWnd, GWL_STYLE, windowLongPtr);
        SetWindowLongPtr(hWnd, GWL_HWNDPARENT, parent);
    }

    public static void setWindowIsPopup(long hWnd, long parent) {
        long windowLongPtr = GetWindowLongPtr(hWnd, GWL_STYLE);
        windowLongPtr = windowLongPtr | WS_CHILD;
        windowLongPtr = windowLongPtr & ~WS_EX_APPWINDOW;
        SetWindowLongPtr(hWnd, GWL_STYLE, windowLongPtr);
        SetWindowLongPtr(hWnd, GWL_HWNDPARENT, parent);
    }

    public static native void open(String path);

    public static native void enableWindow(long hWnd, boolean enabled);

}
