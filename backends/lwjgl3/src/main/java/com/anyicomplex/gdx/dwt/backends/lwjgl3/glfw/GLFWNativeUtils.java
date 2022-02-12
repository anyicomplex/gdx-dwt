package com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.SystemInfo;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;

import static org.lwjgl.system.windows.User32.*;


public class GLFWNativeUtils {

    public static void glfwHideWindowMaximizeButton(long window) {
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
                long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
                SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MAXIMIZEBOX);
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                long display = GLFWNativeX11.glfwGetX11Display();
                long w = GLFWNativeX11.glfwGetX11Window(window);
                break;
            case MAC:
                break;
        }
    }

    public static void glfwHideWindowMinimizeButton(long window) {
        switch (SystemInfo.getSystemType()) {
            case WINDOWS:
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
            SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MINIMIZEBOX);
                break;
            case LINUX:
            case SOLARIS:
            case AIX:
            case OTHER_UNIX:
                long display = GLFWNativeX11.glfwGetX11Display();
                long w = GLFWNativeX11.glfwGetX11Window(window);
                break;
            case MAC:
                break;
        }
    }

}
