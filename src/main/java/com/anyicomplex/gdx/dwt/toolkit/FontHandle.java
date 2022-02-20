package com.anyicomplex.gdx.dwt.toolkit;

import com.badlogic.gdx.files.FileHandle;

public class FontHandle {

    private final String style;
    private final String family;
    private final FileHandle file;
    private final String name;
    private final int spacing;

    public FontHandle(String style, String family, FileHandle file, String name, int spacing) {
        this.style = style;
        this.family = family;
        this.file = file;
        this.name = name;
        this.spacing = spacing;
    }

    public String style() {
        return style;
    }

    public String family() {
        return family;
    }

    public FileHandle file() {
        return file;
    }

    public String name() {
        return name;
    }

    public int spacing() {
        return spacing;
    }

    public boolean mono() {
        return spacing() == 100;
    }

}
