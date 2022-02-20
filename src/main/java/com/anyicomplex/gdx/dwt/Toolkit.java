package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.factory.Shell;
import com.anyicomplex.gdx.dwt.toolkit.FontHandle;
import com.anyicomplex.gdx.dwt.toolkit.Notification;
import com.badlogic.gdx.graphics.Pixmap;

public interface Toolkit {

    Shell rootShell();

    void grabPointer(Shell shell);

    void ungrabPointer();

    String prefsDir(String companyName, String appName);

    String dataDir(String companyName, String appName);

    String cacheDir(String companyName, String appName);

    String tmpDir();

    FontHandle defaultFont();

    FontHandle[] systemFonts();

    void fullscreen();

    void windowed(int width, int height);

    boolean browse(String uri);

    boolean open(String path);

    Notification notification(String title, String message);

    Notification notification(Pixmap icon, String title, String message);

    Notification notification(Pixmap icon, String title, String message, long time);

}
