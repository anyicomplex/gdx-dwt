package com.anyicomplex.gdx.dwt.backends.lwjgl3.system;

import java.nio.ByteBuffer;

public class NativeUtils {

    public static native void freeByteBuffer(ByteBuffer... buffers);

}
