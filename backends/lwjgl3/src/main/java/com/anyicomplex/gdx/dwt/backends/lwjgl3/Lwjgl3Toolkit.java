package com.anyicomplex.gdx.dwt.backends.lwjgl3;

import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.Toolkit;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

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

}
