package com.anyicomplex.gdx.dwt;

import com.anyicomplex.gdx.dwt.toolkit.Font;

public interface Toolkit {

    String prefsDir(String companyName, String appName);

    String dataDir(String companyName, String appName);

    String cacheDir(String companyName, String appName);

    String tmpDir();

    Font defaultFont();

    Font defaultMonoFont();

    Font[] systemFonts();

    void fullscreen();

    void windowed(int width, int height);

    boolean browse(String uri);

}
