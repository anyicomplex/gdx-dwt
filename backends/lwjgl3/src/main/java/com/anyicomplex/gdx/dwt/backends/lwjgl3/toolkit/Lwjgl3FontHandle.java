package com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit;

import com.anyicomplex.gdx.dwt.toolkit.FontHandle;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;

import java.io.File;

public class Lwjgl3FontHandle extends FontHandle {

    public Lwjgl3FontHandle(String style, String family, String path, String name, boolean mono) {
        super(style, family, path == null ? null : new Lwjgl3FileHandle(new File(path).getAbsolutePath(), Files.FileType.Absolute), name, mono);
    }

}
