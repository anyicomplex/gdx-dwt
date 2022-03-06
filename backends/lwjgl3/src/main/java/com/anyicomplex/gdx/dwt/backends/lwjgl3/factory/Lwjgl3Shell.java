package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.Lwjgl3Factory;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw.GLFWNativeUtils;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows.WindowsNatives;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.factory.ShellListener;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;

import java.nio.IntBuffer;

public class Lwjgl3Shell extends Shell {

    private final Array<Shell> childShells = new Array<>();
    private final Shell parentShell;
    private final ShellType shellType;

    private final ApplicationListener applicationListener;
    private final Lwjgl3ApplicationConfiguration lwjgl3Config;
    private final boolean isRootShell;

    private Lwjgl3Window window;

    private final IntBuffer tmpBuffer;
    private final IntBuffer tmpBuffer2;

    @Override
    public ShellType type() {
        return shellType;
    }

    @Override
    public boolean isRootShell() {
        return isRootShell;
    }

    @Override
    public Shell parentShell() {
        return parentShell;
    }

    @Override
    public Array<Shell> childShells() {
        return childShells;
    }

    public Lwjgl3Shell(ApplicationListener listener, ShellConfiguration config) {
        if (config == null) throw new GdxRuntimeException("config cannot be null.");
        this.tmpBuffer = BufferUtils.createIntBuffer(1);
        this.tmpBuffer2 = BufferUtils.createIntBuffer(1);
        applicationListener = listener;
        ShellConfiguration shellConfig = ShellConfiguration.copy(config);
        lwjgl3Config = Lwjgl3Factory.generateLwjgl3Config(shellConfig);
        lwjgl3Config.setInitialVisible(false);
        ShellListener shellListener = shellConfig.shellListener;
        isRootShell = Gdwt.toolkit.rootShell() == null;
        shellType = shellConfig.shellType;
        if (isRootShell) parentShell = null;
        else {
            switch (shellType) {
                case Dialog:
                case Tooltip:
                case Popup:
                    parentShell = shellConfig.parentShell;
                    break;
                default:
                    parentShell = null;
                    break;
            }
        }
        lwjgl3Config.setWindowListener(new Lwjgl3WindowListener() {
            @Override
            public void created(Lwjgl3Window window) {
                Lwjgl3Shell.this.window = window;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        long handle = window.getWindowHandle();
                        if (shellConfig.windowDecorated) GLFWNativeUtils.glfwHideWindowButtons(handle,
                                shellConfig.windowHideMaximizeButton, shellConfig.windowHideMinimizeButton);
                        boolean shouldFocusParent = false;
                        if (!isRootShell && parentShell != null) {
                            parentShell.childShells().add(Lwjgl3Shell.this);
                            switch (shellType) {
                                case Dialog:
                                    GLFWNativeUtils.glfwSetWindowIsDialog(handle, ((Lwjgl3Shell)parentShell).getWindow().getWindowHandle());
                                    break;
                                case Tooltip:
                                    GLFWNativeUtils.glfwSetWindowIsTooltip(handle, ((Lwjgl3Shell)parentShell).getWindow().getWindowHandle());
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
                                    shouldFocusParent = true;
                                    break;
                                case Popup:
                                    GLFWNativeUtils.glfwSetWindowIsPopup(handle, ((Lwjgl3Shell)parentShell).getWindow().getWindowHandle());
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_FALSE);
                                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
                                    shouldFocusParent = true;
                                    break;
                            }
                        }
                        if (shellConfig.initialVisible) GLFW.glfwShowWindow(handle);
                        if (shouldFocusParent) GLFW.glfwFocusWindow(((Lwjgl3Shell)parentShell).getWindow().getWindowHandle());
                    }
                });
                if (shellListener != null) shellListener.created(Lwjgl3Shell.this);
            }
            @Override
            public void iconified(boolean isIconified) {
                if (shellListener != null) shellListener.iconified(isIconified);
            }
            @Override
            public void maximized(boolean isMaximized) {
                if (shellListener != null) shellListener.maximized(isMaximized);
            }
            @Override
            public void focusLost() {
                if (shellListener != null) shellListener.focusLost();
            }
            @Override
            public void focusGained() {
                if (shellListener != null) shellListener.focusGained();
            }
            @Override
            public boolean closeRequested() {
                if (shellListener == null) {
                    closeAllChildShells();
                    if (SharedLibraryLoader.isWindows && shellType == ShellType.Dialog && parentShell != null)
                        WindowsNatives.enableWindow(GLFWNativeWin32.glfwGetWin32Window(((Lwjgl3Shell)parentShell).window.getWindowHandle()), true);
                    return true;
                }
                else {
                    boolean close = shellListener.closeRequested();
                    if (close) {
                        closeAllChildShells();
                        if (SharedLibraryLoader.isWindows && shellType == ShellType.Dialog && parentShell != null)
                            WindowsNatives.enableWindow(GLFWNativeWin32.glfwGetWin32Window(((Lwjgl3Shell)parentShell).window.getWindowHandle()), true);
                    }
                    return close;
                }
            }
            @Override
            public void filesDropped(String[] files) {
                if (shellListener != null) shellListener.filesDropped(files);
            }
            @Override
            public void refreshRequested() {
                if (shellListener != null) shellListener.refreshRequested();
            }
        });
        if (!isRootShell) {
            ((Lwjgl3Application)Gdx.app).newWindow(listener, lwjgl3Config);
        }
    }

    public void loop() {
        if (isRootShell) {
            new Lwjgl3Application(applicationListener, lwjgl3Config);
        }
        else {
            throw new GdxRuntimeException("Not root shell!");
        }
    }

    public Lwjgl3Window getWindow() {
        return window;
    }

    @Override
    public void close() {
        window.closeWindow();
        if (parentShell != null) parentShell.childShells().removeValue(this, true);
        closeAllChildShells();
    }

    @Override
    public int positionX() {
        return window.getPositionX();
    }

    @Override
    public int positionY() {
        return window.getPositionY();
    }

    @Override
    public int width() {
        GLFW.glfwGetWindowSize(window.getWindowHandle(), tmpBuffer, tmpBuffer2);
        return tmpBuffer.get(0);
    }

    @Override
    public int height() {
        GLFW.glfwGetWindowSize(window.getWindowHandle(), tmpBuffer, tmpBuffer2);
        return tmpBuffer2.get(0);
    }

}
