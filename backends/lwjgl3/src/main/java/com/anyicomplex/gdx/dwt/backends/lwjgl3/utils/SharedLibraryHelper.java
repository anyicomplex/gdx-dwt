package com.anyicomplex.gdx.dwt.backends.lwjgl3.utils;

import static com.badlogic.gdx.utils.SharedLibraryLoader.*;

public class SharedLibraryHelper {

    public static String mapGdxLibResName(String libName) {
        if (isWindows) return libName + (is64Bit ? "64.dll" : ".dll");
        if (isLinux) return "lib" + libName + (isARM ? "arm" : "") + (is64Bit ? "64.so" : ".so");
        if (isMac) return "lib" + libName + (is64Bit ? "64.dylib" : ".dylib");
        return libName;
    }

    public static String mapLibName(String libName) {
        if (isWindows) return libName + ".dll";
        if (isLinux) return "lib" + libName + ".so";
        if (isMac) return "lib" + libName + ".dylib";
        return libName;
    }

}
