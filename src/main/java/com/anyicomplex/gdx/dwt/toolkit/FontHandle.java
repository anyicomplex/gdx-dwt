package com.anyicomplex.gdx.dwt.toolkit;

import com.badlogic.gdx.files.FileHandle;

import java.util.Objects;

public class FontHandle {

    private final String style;
    private final String family;
    private final FileHandle file;
    private final String name;
    private final boolean mono;

    public FontHandle(String style, String family, FileHandle file, String name, boolean mono) {
        this.style = style;
        this.family = family;
        this.file = file;
        this.name = name;
        this.mono = mono;
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

    public boolean mono() {
        return mono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontHandle)) return false;
        FontHandle handle = (FontHandle) o;
        return mono == handle.mono && Objects.equals(style, handle.style) && Objects.equals(family, handle.family) && Objects.equals(file, handle.file) && Objects.equals(name, handle.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(style, family, file, name, mono);
    }

}
