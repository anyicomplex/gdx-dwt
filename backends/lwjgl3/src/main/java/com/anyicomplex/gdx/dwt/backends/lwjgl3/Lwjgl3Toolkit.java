package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.Toolkit;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.factory.Lwjgl3Form;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.jnativehook.GlobalInputHandler;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.jnativehook.SharedLibraryLocator;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.macosx.MacOSXNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.windows.WindowsNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3FilePaths;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.Lwjgl3TmpFiles;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.utils.SharedLibraryHelper;
import com.anyicomplex.gdx.dwt.factory.Form;
import com.anyicomplex.gdx.dwt.factory.FormConfiguration;
import com.anyicomplex.gdx.dwt.toolkit.FontHandle;
import com.anyicomplex.gdx.dwt.toolkit.Notification;
import com.anyicomplex.xdg.utils.XDGUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Predicate;

import static com.badlogic.gdx.utils.SharedLibraryLoader.*;

public class Lwjgl3Toolkit implements Toolkit {

    private final Lwjgl3Form rootForm;

    private final GlobalInputHandler inputHandler;

    @Override
    public Form rootForm() {
        return rootForm;
    }

    static  {
        if (Lwjgl3TmpFiles.getTmpStorageDir() == null) throw new IllegalStateException("Resource file extraction path is invalid!");
        // LWJGL3
        System.setProperty("org.lwjgl.system.SharedLibraryExtractPath", Lwjgl3FilePaths.build(
                Lwjgl3TmpFiles.getTmpLibDir().file().getAbsolutePath(),
                "lwjgl3-3.2.3-build-13"));
        // libGDX
        GdxNativesLoader.disableNativesLoading = true;
        String gdxDirTag = "libgdx-" + com.badlogic.gdx.Version.VERSION;
        FileHandle gdxLib = new Lwjgl3FileHandle(SharedLibraryHelper.mapGdxLibResName("gdx"), Files.FileType.Internal);
        System.load(Lwjgl3TmpFiles.getTmpLib(gdxLib, gdxDirTag, gdxLib.name(), false).file().getAbsolutePath());
        // jNativeHook
        System.setProperty("jnativehook.lib.locator", SharedLibraryLocator.class.getCanonicalName());
        // Gdwt
        String gdwtDirTag = "libgdwt-" + com.anyicomplex.gdx.dwt.Version.VERSION;
        FileHandle gdwtLib = new Lwjgl3FileHandle(SharedLibraryHelper.mapGdxLibResName("gdwt"), Files.FileType.Internal);
        System.load(Lwjgl3TmpFiles.getTmpLib(gdwtLib, gdwtDirTag, gdwtLib.name(), false).file().getAbsolutePath());
        if (isLinux) {
            // xdg-utils
            XDGUtils.setScriptDirPath(Lwjgl3FilePaths.build(
                    Lwjgl3TmpFiles.getTmpBinDir().file().getAbsolutePath(),
                    "xdg-utils-" + XDGUtils.SCRIPT_VERSION));
            LinuxNatives.validXDGUtilsScripts();
        }
    }

    public Lwjgl3Toolkit(ApplicationListener listener, FormConfiguration config) {
        if (System.getProperty("os.name").equalsIgnoreCase("freebsd")) isLinux = true;
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException e) {
            e.printStackTrace();
            System.exit(1);
        }
        inputHandler = new GlobalInputHandler();
        GlobalScreen.addNativeMouseListener(inputHandler);
        GlobalScreen.addNativeKeyListener(inputHandler);
        Gdwt.toolkit = this;
        Gdwt.factory = new Lwjgl3Factory();
        rootForm = new Lwjgl3Form(listener, config);
        rootForm.loop();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String prefsDir(String companyName, String appName) {
        return Lwjgl3FilePaths.buildAppConfigPath(companyName, appName);
    }

    @Override
    public String dataDir(String companyName, String appName) {
        return Lwjgl3FilePaths.buildAppDataPath(companyName, appName);
    }

    @Override
    public String cacheDir(String companyName, String appName) {
        return Lwjgl3FilePaths.buildAppCachePath(companyName, appName);
    }

    @Override
    public String tmpDir() {
        return Lwjgl3FilePaths.tmpDirPath();
    }

    @Override
    public FontHandle defaultFont() {
        FontHandle result = null;
        if (isWindows) {
            result = WindowsNatives.getDefaultFont();
        }
        else if (isLinux) {
            result = LinuxNatives.getGtkDefaultFont();
        }
        else if (isMac) {

        }
        return result == null ? null : (result.file() == null ? null : result);
    }

    @Override
    public FontHandle[] systemFonts() {
        FontHandle[] systemFonts = null;
        if (isWindows) {
            systemFonts = WindowsNatives.getSystemFonts();
        }
        else if (isLinux) {
            systemFonts = LinuxNatives.getSystemFonts();
        }
        else if (isMac) {

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
        if (isWindows) {
            WindowsNatives.open(path);
        }
        else if (isLinux) {
            LinuxNatives.open(path);
            return true;
        }
        else if (isMac) {
            MacOSXNatives.open(path);
        }
        return false;
    }

    @Override
    public Notification notification(String title, String message) {
        return null;
    }

    @Override
    public Notification notification(FileHandle icon, String title, String message) {
        return null;
    }

    @Override
    public Notification notification(FileHandle icon, String title, String message, long time) {
        return null;
    }

    @Override
    public void setGlobalInputProcessor(InputProcessor processor) {
        inputHandler.setInputProcessor(processor);
    }

    @Override
    public InputProcessor getGlobalInputProcessor() {
        return inputHandler.getInputProcessor();
    }

}
