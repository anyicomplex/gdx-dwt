package com.anyicomplex.gdx.dwt.backends.lwjgl3.toolkit;

import com.anyicomplex.gdx.dwt.toolkit.Font;

public class Lwjgl3Font implements Font {

    public String style;
    public String family;
    public String path;
    public String name;
    public int spacing;

    public Lwjgl3Font(String style, String family, String path, String name, int spacing) {
        this.style = style;
        this.family = family;
        this.path = path;
        this.name = name;
        this.spacing = spacing;
    }

    @Override
    public String style() {
        return style;
    }

    @Override
    public String family() {
        return family;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int spacing() {
        return spacing;
    }

}
