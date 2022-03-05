package com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows.WindowsNatives;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;


public class GLFWNativeUtils {

    public static void glfwHideWindowButtons(long window, boolean maximize, boolean minimize) {
        if (SharedLibraryLoader.isWindows) {
            long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
            WindowsNatives.hideWindowButtons(hWnd, maximize, minimize);
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
            long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
            long parentHWnd = GLFWNativeWin32.glfwGetWin32Window(parent);
            WindowsNatives.setWindowIsDialog(hWnd, parentHWnd);
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

    public static void glfwSetWindowIsNormal(long window) {
        if (SharedLibraryLoader.isWindows) {
            long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
            WindowsNatives.setWindowIsNormal(hWnd);
        }
        else if (SharedLibraryLoader.isLinux) {
            long display = GLFWNativeX11.glfwGetX11Display();
            long w = GLFWNativeX11.glfwGetX11Window(window);
            LinuxNatives.setXWindowIsNormal(display, w);
        }
        else if (SharedLibraryLoader.isMac) {

        }
    }

    public static void glfwSetWindowIsTooltip(long window, long parent) {
        if (SharedLibraryLoader.isWindows) {
            long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
            long parentHWnd = GLFWNativeWin32.glfwGetWin32Window(parent);
            WindowsNatives.setWindowIsTooltip(hWnd, parentHWnd);
        }
        else if (SharedLibraryLoader.isLinux) {
            long display = GLFWNativeX11.glfwGetX11Display();
            long w = GLFWNativeX11.glfwGetX11Window(window);
            long wparent = GLFWNativeX11.glfwGetX11Window(parent);
            LinuxNatives.setXWindowIsTooltip(display, w, wparent);
        }
        else if (SharedLibraryLoader.isMac) {

        }
    }

    public static void glfwSetWindowIsPopup(long window, long parent) {
        if (SharedLibraryLoader.isWindows) {
            long hWnd = GLFWNativeWin32.glfwGetWin32Window(window);
            long parentHWnd = GLFWNativeWin32.glfwGetWin32Window(parent);
            WindowsNatives.setWindowIsPopup(hWnd, parentHWnd);
        }
        else if (SharedLibraryLoader.isLinux) {
            long display = GLFWNativeX11.glfwGetX11Display();
            long w = GLFWNativeX11.glfwGetX11Window(window);
            long wparent = GLFWNativeX11.glfwGetX11Window(parent);
            LinuxNatives.setXWindowIsPopup(display, w, wparent);
        }
        else if (SharedLibraryLoader.isMac) {

        }
    }

}
