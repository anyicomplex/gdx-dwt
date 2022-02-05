package com.anyicomplex.gdx.dwt;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public interface Toolkit {

    String prefsDir();

    FileHandle defaultRegularFont();

    FileHandle defaultMonoFont();

    Array<FileHandle> systemFonts();

    void fullscreen();

    void windowed(int width, int height);

    boolean openURL(String url);

}
