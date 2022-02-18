package com.anyicomplex.gdx.dwt.backends.lwjgl3.factory;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.Lwjgl3Factory;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw.GLFWNativeUtils;
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
import org.lwjgl.glfw.GLFW;

public class Lwjgl3Shell implements Shell {

    private final Array<Shell> childShells = new Array<>();
    private final Shell parentShell;
    private final ShellType shellType;

    private final ApplicationListener applicationListener;
    private final Lwjgl3ApplicationConfiguration lwjgl3Config;
    private final boolean isRootShell;

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
    public Array<Shell> getChildShells() {
        return childShells;
    }

    public Lwjgl3Shell(ApplicationListener listener, ShellConfiguration config) {
        if (config == null) throw new GdxRuntimeException("config cannot be null.");
        applicationListener = listener;
        ShellConfiguration shellConfig = ShellConfiguration.copy(config);
        lwjgl3Config = Lwjgl3Factory.generateLwjgl3Config(shellConfig);
        lwjgl3Config.setInitialVisible(false);
        ShellListener shellListener = shellConfig.shellListener;
        isRootShell = Gdwt.toolkit.rootShell() == null;
        shellType = shellConfig.shellType;
        parentShell = isRootShell ? null : (shellType == ShellType.Dialog ? shellConfig.parentShell : null);
        lwjgl3Config.setWindowListener(new Lwjgl3WindowListener() {
            @Override
            public void created(Lwjgl3Window window) {
                Lwjgl3Shell.this.window = window;
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        long handle = window.getWindowHandle();
                        GLFWNativeUtils.glfwHideWindowButtons(handle, shellConfig.windowHideMaximizeButton,
                                shellConfig.windowHideMinimizeButton);
                        if (!isRootShell && parentShell != null) {
                            parentShell.getChildShells().add(Lwjgl3Shell.this);
                            if (shellConfig.shellType == ShellType.Dialog) {
                                GLFWNativeUtils.glfwSetWindowIsDialog(handle, ((Lwjgl3Shell)parentShell).getWindow().getWindowHandle());
                            }
                        }
                        if (shellConfig.initialVisible) GLFW.glfwShowWindow(handle);
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
                closeAllChildShells();
                return shellListener == null || shellListener.closeRequested();
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

    private Lwjgl3Window window;

    public Lwjgl3Window getWindow() {
        return window;
    }

    @Override
    public void close() {
        window.closeWindow();
        closeAllChildShells();
    }

}
