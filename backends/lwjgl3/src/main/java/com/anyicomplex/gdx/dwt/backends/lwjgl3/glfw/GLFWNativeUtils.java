package com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;

import static org.lwjgl.system.windows.User32.*;


public class GLFWNativeUtils {

    public static void glfwHideWindowButtons(long window, boolean maximize, boolean minimize) {
        if (SharedLibraryLoader.isWindows) {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
            if (maximize) SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MAXIMIZEBOX);
            else SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) | WS_MAXIMIZEBOX);
            if (minimize) SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_MINIMIZEBOX);
            else SetWindowLongPtr(hwnd, GWL_STYLE, GetWindowLongPtr(hwnd, GWL_STYLE) | WS_MINIMIZEBOX);
        }
        else if (SharedLibraryLoader.isLinux) {
            long display = GLFWNativeX11.glfwGetX11Display();
            long w = GLFWNativeX11.glfwGetX11Window(window);
            LinuxNatives.hideXWindowButtons(display, w, maximize, minimize);
        }
        else if (SharedLibraryLoader.isMac) {
            
        }
    }

    public static void glfwSetWindowIsDialog(long window, long parent) {
        if (SharedLibraryLoader.isWindows) {

        }
        else if (SharedLibraryLoader.isLinux) {
            long display = GLFWNativeX11.glfwGetX11Display();
            long w = GLFWNativeX11.glfwGetX11Window(window);
            long wparent = GLFWNativeX11.glfwGetX11Window(parent);
            LinuxNatives.setXWindowIsDialog(display, w, wparent);
        }
        else if (SharedLibraryLoader.isMac) {

        }
    }

}
