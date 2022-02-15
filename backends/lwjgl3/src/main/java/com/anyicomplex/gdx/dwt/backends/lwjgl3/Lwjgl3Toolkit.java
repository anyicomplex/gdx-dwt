package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.Toolkit;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.PathHelper;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.util.SystemPath;
import com.anyicomplex.gdx.dwt.toolkit.Font;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.net.URI;
import java.net.URISyntaxException;

public class Lwjgl3Toolkit implements Toolkit {

    public Lwjgl3Toolkit() {
        Gdwt.toolkit = this;
        Gdwt.factory = new Lwjgl3Factory();
    }

    public void loop(ApplicationListener listener, Lwjgl3ApplicationConfiguration config) {
        if (System.getProperty("os.name").equalsIgnoreCase("freebsd")) SharedLibraryLoader.isLinux = true;
        new Lwjgl3Application(listener, config);
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
    public Font defaultFont() {
        if (SharedLibraryLoader.isWindows) {

        }
        else if (SharedLibraryLoader.isLinux) {
            return LinuxNatives.getGtkDefaultFont();
        }
        else if (SharedLibraryLoader.isMac) {

        }
        return null;
    }

    @Override
    public Font[] systemFonts() {
        if (SharedLibraryLoader.isWindows) {

        }
        else if (SharedLibraryLoader.isLinux) {
            return LinuxNatives.systemFonts();
        }
        else if (SharedLibraryLoader.isMac) {

        }
        return null;
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
        } catch (URISyntaxException ignored) {
            return false;
        }
        return false;
    }

}
