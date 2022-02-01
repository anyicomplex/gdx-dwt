package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;

import static org.lwjgl.system.windows.User32.*;


public class GLFWNativeUtils {

    public static void glfwHideWindowMaximizeButton(long window) {
        if (SharedLibraryLoader.isWindows) {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
            SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MAXIMIZEBOX);
        }
        else if (SharedLibraryLoader.isLinux) {
            long X11Display = GLFWNativeX11.glfwGetX11Display();
            long X11Window = GLFWNativeX11.glfwGetX11Window(window);

        }
    }

    public static void glfwHideWindowMinimizeButton(long window) {
        if (SharedLibraryLoader.isWindows) {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
            SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MINIMIZEBOX);
        }
    }

}
