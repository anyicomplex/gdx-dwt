package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.Toolkit;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.factory.Lwjgl3Shell;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.glfw.GLFWNativeUtils;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.macosx.MacOSXNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows.WindowsNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.PathHelper;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.SystemPath;
import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.anyicomplex.gdx.dwt.toolkit.FontHandle;
import com.anyicomplex.gdx.dwt.toolkit.Notification;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Predicate;

public class Lwjgl3Toolkit implements Toolkit {

    private final Lwjgl3Shell rootShell;

    @Override
    public Shell rootShell() {
        return rootShell;
    }

    @Override
    public void grabPointer(Shell shell) {
        GLFWNativeUtils.glfwGrabPointer(((Lwjgl3Shell)shell).getWindow().getWindowHandle());
    }

    @Override
    public void ungrabPointer() {
        GLFWNativeUtils.glfwUngrabPointer();
    }

    public Lwjgl3Toolkit(ApplicationListener listener, ShellConfiguration config) {
        if (System.getProperty("os.name").equalsIgnoreCase("freebsd")) SharedLibraryLoader.isLinux = true;
        Gdwt.toolkit = this;
        Gdwt.factory = new Lwjgl3Factory();
        rootShell = new Lwjgl3Shell(listener, config);
        rootShell.loop();
    }

    @Override
    public String prefsDir(String companyName, String appName) {
        return PathHelper.buildAppConfigPath(companyName, appName);
    }

    @Override
    public String dataDir(String companyName, String appName) {
        return PathHelper.buildAppDataPath(companyName, appName);
    }

    @Override
    public String cacheDir(String companyName, String appName) {
        return PathHelper.buildAppCachePath(companyName, appName);
    }

    @Override
    public String tmpDir() {
        return SystemPath.temporary();
    }

    @Override
    public FontHandle defaultFont() {
        FontHandle result = null;
        if (SharedLibraryLoader.isWindows) {
            result = WindowsNatives.getDefaultFont();
        }
        else if (SharedLibraryLoader.isLinux) {
            result = LinuxNatives.getGtkDefaultFont();
        }
        else if (SharedLibraryLoader.isMac) {

        }
        return result == null ? null : (result.file() == null ? null : result);
    }

    @Override
    public FontHandle[] systemFonts() {
        FontHandle[] systemFonts = null;
        if (SharedLibraryLoader.isWindows) {
            systemFonts = WindowsNatives.getSystemFonts();
        }
        else if (SharedLibraryLoader.isLinux) {
            systemFonts = LinuxNatives.getSystemFonts();
        }
        else if (SharedLibraryLoader.isMac) {

        }
        if (systemFonts == null) return null;
        return Arrays.stream(systemFonts).distinct().filter(new Predicate<FontHandle>() {
            @Override
            public boolean test(FontHandle fontHandle) {
                FileHandle file = fontHandle.file();
                return file != null && file.exists() && !file.isDirectory();
            }
        }).toArray(FontHandle[]::new);
    }

    @Override
    public void fullscreen() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }

    @Override
    public void windowed(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
    }

    @Override
    public boolean browse(String uri) {
        try {
            uri = new URI(uri).toString();
            return open(uri);
        } catch (URISyntaxException ignored) {
            return false;
        }
    }

    @Override
    public boolean open(String path) {
        if (SharedLibraryLoader.isWindows) {
            WindowsNatives.open(path);
        }
        else if (SharedLibraryLoader.isLinux) {
            LinuxNatives.open(path);
            return true;
        }
        else if (SharedLibraryLoader.isMac) {
            MacOSXNatives.open(path);
        }
        return false;
    }

    @Override
    public Notification notification(String title, String message) {
        return null;
    }

    @Override
    public Notification notification(Pixmap icon, String title, String message) {
        return null;
    }

    @Override
    public Notification notification(Pixmap icon, String title, String message, long time) {
        return null;
    }

}
