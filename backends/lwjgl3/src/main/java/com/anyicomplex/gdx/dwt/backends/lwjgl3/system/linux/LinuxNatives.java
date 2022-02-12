package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

import com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit.Lwjgl3Font;
import com.anyicomplex.gdx.dwt.toolkit.Font;

public class LinuxNatives {

    public static Font[] systemFonts() {
        return nsystemFonts();
    }

    public static native Lwjgl3Font[] nsystemFonts();

}
