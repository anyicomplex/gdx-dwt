package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.Toolkit;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux.LinuxNatives;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.net.MalformedURLException;
import java.net.URL;

public class Lwjgl3Toolkit implements Toolkit {

    public Lwjgl3Toolkit() {
        Gdwt.toolkit = this;
        Gdwt.factory = new Lwjgl3Factory();
    }

    public void loop(ApplicationListener listener, Lwjgl3ApplicationConfiguration config) {
        new Lwjgl3Application(listener, config);
    }

    @Override
    public String prefsDir() {
        return System.getProperty("user.home") + ".prefs";
    }

    @Override
    public FileHandle defaultRegularFont() {
        return null;
    }

    @Override
    public FileHandle defaultMonoFont() {
        return null;
    }

    @Override
    public Array<FileHandle> systemFonts() {
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
    public boolean openURL(String url) {
        if (SharedLibraryLoader.isWindows) {

        }
        else if (SharedLibraryLoader.isLinux) {
            try {
                return LinuxNatives.openURL(new URL(url).toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (SharedLibraryLoader.isMac) {

        }
        return false;
    }

}
