package com.anyicomplex.gdx.dwt.toolkit;

public interface Font {

    String style();

    String family();

    String path();

    String name();

    int spacing();

    default boolean mono() {
        return spacing() == 100;
    }

}
