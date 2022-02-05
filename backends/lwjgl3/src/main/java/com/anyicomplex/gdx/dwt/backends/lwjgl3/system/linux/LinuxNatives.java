package com.anyicomplex.gdx.dwt.backends.lwjgl3.system.linux;

public class LinuxNatives {

    private LinuxNatives() {
        throw new UnsupportedOperationException();
    }

    public static native String[] systemFonts();

    public static native long XInternAtom(long display, String atom_name, boolean only_if_exists);

    public static native void XFlush(long display);

    public static native int XChangeProperty(long display, long w, long property, long type, int format, int mode, String data, int nelements);

    public static native boolean openURL(String URL);

}
