package com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.SystemInfo;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;

import static org.lwjgl.system.windows.User32.*;


public class GLFWNativeUtils {

    public static void glfwHideWindowButtons(long window, boolean maximize, boolean minimize) {
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
                long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
                if (maximize) SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MAXIMIZEBOX);
                if (minimize) SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MINIMIZEBOX);
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                long display = GLFWNativeX11.glfwGetX11Display();
                long w = GLFWNativeX11.glfwGetX11Window(window);
                LinuxNatives.nhideXWindowButtons(display, w, maximize, minimize);
                break;
            case MAC:
                break;
        }
    }

}
